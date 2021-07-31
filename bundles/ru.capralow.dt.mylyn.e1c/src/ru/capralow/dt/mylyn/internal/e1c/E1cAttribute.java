/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Aleksandr Kapralov
 *
 */
public enum E1cAttribute
{
    IID("Код", TaskAttribute.TASK_KEY, TaskAttribute.TYPE_SHORT_TEXT, E1cFlag.READ_ONLY),
    TITLE("Наименование", TaskAttribute.SUMMARY, TaskAttribute.TYPE_SHORT_RICH_TEXT);

    private final String prettyName;

    private final String taskKey;

    private final String type;

    private Set<E1cFlag> flags;

    public String getKind()
    {
        return null;
    }

    public boolean isReadOnly()
    {
        return flags.contains(E1cFlag.READ_ONLY);
    }

    E1cAttribute(String prettyName, String taskKey, String type, E1cFlag... flags)
    {
        this.taskKey = taskKey;
        this.prettyName = prettyName;
        this.type = type;
        if (flags == null || flags.length == 0)
        {
            this.setFlags(EnumSet.noneOf(E1cFlag.class));
        }
        else
        {
            this.setFlags(EnumSet.copyOf(Arrays.asList(flags)));
        }
    }

    E1cAttribute(String prettyName, String taskKey, String type)
    {
        this(prettyName, taskKey, type, new E1cFlag[] { });
    }

    public Set<E1cFlag> getFlags()
    {
        return flags;
    }

    public void setFlags(Set<E1cFlag> flags)
    {
        this.flags = flags;
    }

    public String getPrettyName()
    {
        return prettyName;
    }

    public String getTaskKey()
    {
        return taskKey;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return this.prettyName;
    }

    public static E1cAttribute get(String key)
    {
        for (E1cAttribute attr : E1cAttribute.values())
        {
            if (attr.getTaskKey().equals(key))
            {
                return attr;
            }
        }
        return null;
    }
}
