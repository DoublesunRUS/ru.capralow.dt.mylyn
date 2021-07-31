/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Composite;

import ru.capralow.dt.mylyn.e1c.E1cConnector;

/**
 * @author Aleksandr Kapralov
 *
 */
public class E1cRepositorySettingsPage
    extends AbstractRepositorySettingsPage
{

    public E1cRepositorySettingsPage(String title, String description, TaskRepository repository)
    {
        super(title, description, repository);

        setNeedsValidateOnFinish(true);
    }

    @Override
    public String getConnectorKind()
    {
        return E1cConnector.CONNECTOR_KIND;
    }

    @Override
    protected void createAdditionalControls(Composite parent)
    {
        // TODO Автоматически созданная заглушка метода

    }

    @Override
    protected Validator getValidator(final TaskRepository repository)
    {
        return new Validator()
        {

            @Override
            public void run(IProgressMonitor monitor) throws CoreException
            {
                E1cConnector.validate(repository);
            }

        };
    }
}
