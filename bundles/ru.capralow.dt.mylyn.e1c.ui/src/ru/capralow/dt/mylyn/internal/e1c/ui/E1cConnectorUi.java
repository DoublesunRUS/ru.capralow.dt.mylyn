/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;

import ru.capralow.dt.mylyn.e1c.E1cConnector;

/**
 * @author Aleksandr Kapralov
 *
 */
public class E1cConnectorUi
    extends AbstractRepositoryConnectorUi
{

    @Override
    public String getConnectorKind()
    {
        return E1cConnector.CONNECTOR_KIND;
    }

    @Override
    public ITaskRepositoryPage getSettingsPage(TaskRepository repository)
    {
        return new E1cRepositorySettingsPage("Новый репозиторий", "Настройки подключения", repository);
    }

    @Override
    public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query)
    {
        // TODO Автоматически созданная заглушка метода
        return null;
    }

    @Override
    public IWizard getNewTaskWizard(TaskRepository repository, ITaskMapping selection)
    {
        // TODO Автоматически созданная заглушка метода
        return null;
    }

    @Override
    public boolean hasSearchPage()
    {
        return false;
    }

}
