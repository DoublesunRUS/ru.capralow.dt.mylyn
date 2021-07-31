/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * @author Aleksandr Kapralov
 *
 */
public class E1cAttributeMapper
    extends TaskAttributeMapper
{

    public E1cAttributeMapper(TaskRepository repository)
    {
        super(repository);
    }

}
