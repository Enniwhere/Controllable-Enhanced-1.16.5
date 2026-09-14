package com.mrcrayfish.controllable.client;

import net.minecraft.util.IStringSerializable;

/**
 * Author: Simon Enni
 */
public enum MovementSource implements IStringSerializable
{
    THUMBSTICK("controllable.movement_source.thumbstick"),
    DPAD("controllable.movement_source.dpad"),
    BOTH("controllable.movement_source.both");

    String key;

    MovementSource(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return this.key;
    }

    @Override
    public String getString()
    {
        return this.key;
    }
}
