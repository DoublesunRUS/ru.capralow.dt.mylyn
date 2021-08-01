/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.e1c;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

import ru.capralow.dt.mylyn.internal.e1c.E1cAttributeMapper;

/**
 * @author Aleksandr Kapralov
 *
 */
public interface IE1cConnection
{

    E1cAttributeMapper getMapper();

    List<E1cError> getErrors() throws CoreException;

    E1cError getError(String refKey) throws CoreException;

    void update() throws CoreException;

}
