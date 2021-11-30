package ru.simplecloudstorage.commands;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS,
        property = "commandClass")
public abstract class BaseCommand implements Serializable {

    private final CommandType type;

    protected BaseCommand(CommandType t) {
        this.type = t;
    }

    public CommandType getType() {
        return type;
    }

}
