package com.tankwars.input;

/** Ordered fire-button transitions buffered between simulation steps. */
public enum FireInputEvent {
    NONE,
    PRESS,
    RELEASE,
    CANCEL
}
