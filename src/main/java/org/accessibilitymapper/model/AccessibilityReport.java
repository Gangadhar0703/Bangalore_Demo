package org.accessibilitymapper.model;

public record AccessibilityReport(
        Boolean wheelchairRamp,
        Boolean workingElevator,
        Boolean accessibleWashroom,
        Boolean tactilePaving,
        Boolean wideDoorways) {}