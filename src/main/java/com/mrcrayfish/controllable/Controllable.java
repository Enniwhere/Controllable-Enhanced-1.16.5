package com.mrcrayfish.controllable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import com.google.common.io.ByteStreams;
import com.mrcrayfish.controllable.client.ButtonBinding;
import com.mrcrayfish.controllable.client.Buttons;
import com.mrcrayfish.controllable.client.Controller;
import com.mrcrayfish.controllable.client.ControllerEvents;
import com.mrcrayfish.controllable.client.ControllerInput;
import com.mrcrayfish.controllable.client.ControllerManager;
import com.mrcrayfish.controllable.client.ControllerProperties;
import com.mrcrayfish.controllable.client.ControllerToast;
import com.mrcrayfish.controllable.client.GuiEvents;
import com.mrcrayfish.controllable.client.IControllerListener;
import com.mrcrayfish.controllable.client.Mappings;
import com.mrcrayfish.controllable.client.MovementSource;
import com.mrcrayfish.controllable.client.RadialMenuHandler;
import com.mrcrayfish.controllable.client.RenderEvents;
import com.mrcrayfish.controllable.client.gui.ButtonBindingScreen;
import com.mrcrayfish.controllable.client.gui.ControllerLayoutScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

/**
 * Author: MrCrayfish
 */
@Mod(Reference.MOD_ID)
public class Controllable implements IControllerListener
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    /**
     * System property used to select which connected controller this game instance should use by
     * default. The value is a 1-based index of the connected controllers (i.e. the order they are
     * listed in the controller selection screen). This allows multiple Minecraft instances to
     * target different physical controllers, even when they are the same model.
     */
    public static final String CONTROLLER_PROPERTY = "controllable.controller";

    private static ControllerManager manager;
    private static Controller controller;
    private static ControllerInput input;
    private static File configFolder;
    private static boolean jeiLoaded;

    /**
     * Guards the initial controller selection. The selection is deferred until the client tick
     * phase (and retried each tick until it succeeds) so that GLFW's joystick state is live;
     * otherwise {@link GLFW#glfwJoystickIsGamepad(int)} can report stale data during early mod
     * setup, and on Linux devices may only finish enumerating as gamepads a few ticks after
     * startup.
     */
    private static boolean initialControllerSelected = false;

    /**
     * Ensures the "not a connected gamepad" warning is only logged once per game session, since
     * the initial selection may be retried each tick while waiting for a device to enumerate.
     */
    private static boolean warnedControllerNotReady = false;

    /** Ticker used to throttle periodic diagnostics while waiting for the requested controller. */
    private static int selectionRetryCounter = 0;

    public Controllable()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    @Nullable
    public static Controller getController()
    {
        return controller;
    }

    public static ControllerInput getInput()
    {
        return input;
    }

    public static File getConfigFolder()
    {
        return configFolder;
    }

    public static boolean isJeiLoaded()
    {
        return jeiLoaded;
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            Minecraft mc = event.getMinecraftSupplier().get();
            configFolder = new File(mc.gameDir, "config");
            jeiLoaded = ModList.get().isLoaded("jei");

            ControllerProperties.load(configFolder);

            try(InputStream is = Mappings.class.getResourceAsStream("/gamecontrollerdb.txt"))
            {
                if(is != null)
                {
                    byte[] bytes = ByteStreams.toByteArray(is);
                    ByteBuffer buffer = MemoryUtil.memASCIISafe(new String(bytes));
                    if(buffer != null && GLFW.glfwUpdateGamepadMappings(buffer))
                    {
                        LOGGER.info("Successfully updated gamepad mappings");
                    }
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            /* Loads up the controller manager and adds a listener */
            Controllable.manager = new ControllerManager();
            Controllable.manager.addControllerListener(this);

            /* The initial controller selection is deferred to the first client tick (see controllerTick) */

            Mappings.load(configFolder);

            /* Registers events */
            MinecraftForge.EVENT_BUS.register(this);
            MinecraftForge.EVENT_BUS.register(input = new ControllerInput());
            MinecraftForge.EVENT_BUS.register(new RenderEvents());
            MinecraftForge.EVENT_BUS.register(new GuiEvents(Controllable.manager));
            MinecraftForge.EVENT_BUS.register(new ControllerEvents());
            MinecraftForge.EVENT_BUS.register(RadialMenuHandler.instance());
            MinecraftForge.EVENT_BUS.addListener(this::controllerTick);
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void connected(int jid)
    {
        Minecraft.getInstance().enqueue(() ->
        {
            if(Controllable.controller == null)
            {
                /*
                 * If a controller was explicitly requested via the system property, only that
                 * controller may be selected. Selecting any newly connected controller here
                 * would cause multiple game instances to all grab the first available one.
                 * The preferred controller is (re)tried each tick by selectDefaultController().
                 */
                if(System.getProperty(CONTROLLER_PROPERTY) == null && Config.CLIENT.options.autoSelect.get())
                {
                    setController(new Controller(jid));
                }

                Minecraft mc = Minecraft.getInstance();
                if(mc.player != null && Controllable.controller != null)
                {
                    Minecraft.getInstance().getToastGui().add(new ControllerToast(true, Controllable.controller.getName()));
                }
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void disconnected(int jid)
    {
        Minecraft.getInstance().enqueue(() ->
        {
            if(Controllable.controller != null)
            {
                if(Controllable.controller.getJid() == jid)
                {
                    Controller oldController = Controllable.controller;

                    setController(null);

                    /*
                     * When a controller was explicitly requested via the system property, never
                     * grab an arbitrary controller on disconnect - the preferred controller is
                     * retried each tick instead. Otherwise this could steal the controller that
                     * another game instance (with a different property value) is using.
                     */
                    if(System.getProperty(CONTROLLER_PROPERTY) == null && Config.CLIENT.options.autoSelect.get() && manager.getControllerCount() > 0)
                    {
                        Optional<Integer> optional = manager.getControllers().keySet().stream().min(Comparator.comparing(i -> i));
                        optional.ifPresent(minJid -> setController(new Controller(minJid)));
                    }

                    Minecraft mc = Minecraft.getInstance();
                    if(mc.player != null)
                    {
                        Minecraft.getInstance().getToastGui().add(new ControllerToast(false, oldController.getName()));
                    }
                }
            }
        });
    }

    /**
     * Selects the default controller on start up. If the {@link #CONTROLLER_PROPERTY} system
     * property is set, the controller at that 1-based index is selected. Otherwise falls back to
     * the first connected controller, matching the original behaviour.
     */
    private static void selectDefaultController()
    {
        if(selectPreferredController())
        {
            initialControllerSelected = true;
            return;
        }

        /*
         * If a controller was explicitly requested via the system property, never fall back to
         * another controller - doing so would cause multiple game instances to all select the
         * first controller. Instead keep waiting until the requested device enumerates.
         */
        if(System.getProperty(CONTROLLER_PROPERTY) != null)
        {
            return;
        }

        initialControllerSelected = true;

        if(!Config.CLIENT.options.autoSelect.get())
            return;

        if(GLFW.glfwJoystickPresent(GLFW.GLFW_JOYSTICK_1) && GLFW.glfwJoystickIsGamepad(GLFW.GLFW_JOYSTICK_1))
        {
            setController(new Controller(GLFW.GLFW_JOYSTICK_1));
        }
    }

    /**
     * Attempts to select the controller specified via the {@link #CONTROLLER_PROPERTY} system
     * property. The value may be:
     * <ul>
     *   <li>a GLFW joystick ID (0-based), e.g. {@code -Dcontrollable.controller=1}</li>
     *   <li>a (partial, case-insensitive) controller name, e.g.
     *       {@code -Dcontrollable.controller=Xbox}</li>
     *   <li>a controller name with a 1-based occurrence index for identical models, e.g.
     *       {@code -Dcontrollable.controller=Xbox#2} selects the second matching controller</li>
     * </ul>
     * Name matching is more robust than joystick IDs across multiple game instances, since GLFW
     * joystick IDs are process-local and their assignment order is not guaranteed.
     *
     * @return true if a controller was selected
     */
    private static boolean selectPreferredController()
    {
        String value = System.getProperty(CONTROLLER_PROPERTY);
        if(value == null || value.isEmpty())
            return false;

        /* Parse "#n" occurrence suffix for identically named controllers */
        String nameQuery = value;
        int occurrence = 1;
        int hashIndex = value.lastIndexOf('#');
        if(hashIndex >= 0)
        {
            String suffix = value.substring(hashIndex + 1).trim();
            try
            {
                occurrence = Integer.parseInt(suffix);
                nameQuery = value.substring(0, hashIndex);
                if(occurrence < 1)
                {
                    LOGGER.warn("Invalid occurrence '{}' in system property '{}', must be 1 or greater", suffix, CONTROLLER_PROPERTY);
                    return false;
                }
            }
            catch(NumberFormatException e)
            {
                /* Not "#number" - treat the whole string (including '#') as the name */
            }
        }

        /* Try numeric joystick ID first */
        Integer jidById = null;
        try
        {
            jidById = Integer.parseInt(nameQuery.trim());
        }
        catch(NumberFormatException ignored) {}

        if(jidById != null)
        {
            int jid = jidById;
            if(jid < GLFW.GLFW_JOYSTICK_1 || jid > GLFW.GLFW_JOYSTICK_LAST)
            {
                LOGGER.warn("Invalid value '{}' for system property '{}', expected a GLFW joystick ID between {} and {}", nameQuery, CONTROLLER_PROPERTY, GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_JOYSTICK_LAST);
                return false;
            }

            if(!GLFW.glfwJoystickIsGamepad(jid))
            {
                logWaiting("Requested joystick ID {} is not a connected gamepad yet", String.valueOf(jid));
                return false;
            }

            setController(new Controller(jid));
            initialControllerSelected = true;
            LOGGER.info("Selected controller at joystick ID {} (name '{}') from system property '{}'", jid, GLFW.glfwGetGamepadName(jid), CONTROLLER_PROPERTY);
            return true;
        }

        /* Otherwise match by (partial, case-insensitive) controller name */
        String query = nameQuery.trim().toLowerCase();
        int matchCount = 0;
        for(int jid = GLFW.GLFW_JOYSTICK_1; jid <= GLFW.GLFW_JOYSTICK_LAST; jid++)
        {
            if(!GLFW.glfwJoystickIsGamepad(jid))
                continue;

            String name = GLFW.glfwGetGamepadName(jid);
            if(name == null)
                continue;

            if(name.toLowerCase().contains(query))
            {
                matchCount++;
                if(matchCount == occurrence)
                {
                    setController(new Controller(jid));
                    initialControllerSelected = true;
                    LOGGER.info("Selected controller at joystick ID {} (name '{}') from system property '{}' (match {}/{})", jid, name, CONTROLLER_PROPERTY, matchCount, occurrence);
                    return true;
                }
            }
        }

        logWaiting("Requested controller matching '{}' (occurrence {}) not found yet", nameQuery, String.valueOf(occurrence));
        return false;
    }

    /**
     * Logs a periodic diagnostic while waiting for the requested controller, listing every
     * connected joystick as GLFW currently sees it. Throttled to once every 100 ticks (5s) to
     * avoid log spam during the retry loop.
     */
    private static void logWaiting(String message, String... args)
    {
        if(!warnedControllerNotReady)
        {
            warnedControllerNotReady = true;
            LOGGER.info(message + (args.length > 0 ? "" : ""), (Object[]) args);
        }

        selectionRetryCounter++;
        if(selectionRetryCounter % 100 == 0)
        {
            StringBuilder sb = new StringBuilder("Still waiting for requested controller; GLFW joysticks:");
            boolean any = false;
            for(int jid = GLFW.GLFW_JOYSTICK_1; jid <= GLFW.GLFW_JOYSTICK_LAST; jid++)
            {
                boolean present = GLFW.glfwJoystickPresent(jid);
                String gpName = GLFW.glfwGetGamepadName(jid);
                sb.append(String.format(" [jid=%d present=%s gamepadName=%s]", jid, present, gpName != null ? "'" + gpName + "'" : "<none>"));
                any = true;
            }
            if(!any)
                sb.append(" <none polled>");
            LOGGER.info(sb.toString());
        }
    }

    public static void setController(@Nullable Controller controller)
    {
        if(controller != null)
        {
            Controllable.controller = controller;
            Mappings.updateControllerMappings(controller);
        }
        else
        {
            Controllable.controller = null;
        }
    }

    private void controllerTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START)
            return;

        if(manager != null)
        {
            manager.update();

            /*
             * Attempts to select the preferred controller, otherwise the first connected if auto
             * select is enabled. This is retried every tick until it succeeds, because on some
             * systems (notably Linux) joysticks only finish enumerating as gamepads a few ticks
             * after startup, even after GLFW has polled events.
             */
            if(!initialControllerSelected)
            {
                selectDefaultController();
            }
        }
        if(controller != null)
        {
            controller.updateGamepadState();
            gatherAndQueueControllerInput();
        }
    }
    
    private static void gatherAndQueueControllerInput()
    {
        Controller currentController = controller;
        if(currentController == null)
            return;
        ButtonStates states = new ButtonStates();
        states.setState(Buttons.A, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_A));
        states.setState(Buttons.B, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_B));
        states.setState(Buttons.X, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_X));
        states.setState(Buttons.Y, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_Y));
        states.setState(Buttons.SELECT, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_BACK));
        states.setState(Buttons.HOME, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_GUIDE));
        states.setState(Buttons.START, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_START));
        states.setState(Buttons.LEFT_THUMB_STICK, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB));
        states.setState(Buttons.RIGHT_THUMB_STICK, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB));
        states.setState(Buttons.LEFT_BUMPER, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER));
        states.setState(Buttons.RIGHT_BUMPER, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER));
        states.setState(Buttons.LEFT_TRIGGER, currentController.getLTriggerValue() >= 0.5F);
        states.setState(Buttons.RIGHT_TRIGGER, currentController.getRTriggerValue() >= 0.5F);
        states.setState(Buttons.DPAD_UP, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP));
        states.setState(Buttons.DPAD_DOWN, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN));
        states.setState(Buttons.DPAD_LEFT, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT));
        states.setState(Buttons.DPAD_RIGHT, getButtonState(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT));
        processButtonStates(states);
    }

    private static void processButtonStates(ButtonStates states)
    {
        ButtonBinding.tick();
        for(int i = 0; i < Buttons.BUTTONS.length; i++)
        {
            processButton(Buttons.BUTTONS[i], states);
        }
    }

    private static void processButton(int index, ButtonStates newStates)
    {
        boolean state = newStates.getState(index);

        Screen screen = Minecraft.getInstance().currentScreen;
        if(screen instanceof ControllerLayoutScreen)
        {
            ((ControllerLayoutScreen) screen).processButton(index, newStates);
            return;
        }

        if (controller == null)
        {
            return;
        }

        /* Suppress the D-Pad actions while the D-Pad is being used to move the player in game */
        boolean suppressedDpadMovement = screen == null
                && Config.CLIENT.options.movementSource.get() != MovementSource.THUMBSTICK
                && isDpadMovementButton(index);

        if(controller.getMapping() != null)
        {
            index = controller.getMapping().remap(index);
        }

        //No binding so don't perform any action
        if(index == -1)
        {
            return;
        }

        ButtonStates states = controller.getButtonsStates();

        if(state)
        {
            if(!states.getState(index))
            {
                states.setState(index, true);
                if(suppressedDpadMovement)
                {
                    return;
                }
                if(screen instanceof ButtonBindingScreen)
                {
                    if(((ButtonBindingScreen) screen).processButton(index))
                    {
                        return;
                    }
                }
                input.handleButtonInput(controller, index, true, false);
            }
        }
        else if(states.getState(index))
        {
            states.setState(index, false);
            if(suppressedDpadMovement)
            {
                return;
            }
            input.handleButtonInput(controller, index, false, false);
        }
    }

    private static boolean isDpadMovementButton(int button)
    {
        return button == Buttons.DPAD_UP || button == Buttons.DPAD_DOWN || button == Buttons.DPAD_LEFT || button == Buttons.DPAD_RIGHT;
    }

    /**
     * Returns whether a button on the controller is pressed or not. This is a raw approach to
     * getting whether a button is pressed or not. You should use a {@link ButtonBinding} instead.
     *
     * @param button the button to check if pressed
     * @return
     */
    public static boolean isButtonPressed(int button)
    {
        return controller != null && controller.getButtonsStates().getState(button);
    }

    private static boolean getButtonState(int buttonCode)
    {
        return controller != null && controller.getGamepadState().buttons(buttonCode) == GLFW.GLFW_PRESS;
    }
}
