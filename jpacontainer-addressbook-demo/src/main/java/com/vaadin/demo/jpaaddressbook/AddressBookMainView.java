package com.vaadin.demo.jpaaddressbook;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.demo.jpaaddressbook.PersonEditor.EditorSavedEvent;
import com.vaadin.demo.jpaaddressbook.PersonEditor.EditorSavedListener;
import com.vaadin.demo.jpaaddressbook.domain.Department;
import com.vaadin.demo.jpaaddressbook.domain.Person;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

public class AddressBookMainView extends HorizontalSplitPanel implements
        ComponentContainer {

    private Tree groupTree;

    private Table personTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;

    private JPAContainer<Department> departments;
    private JPAContainer<Person> persons;

    private Department departmentFilter;
    private String textFilter;

    public AddressBookMainView() {
        departments = new HierarchicalDepartmentContainer();
        persons = JPAContainerFactory.make(Person.class, JpaAddressbookApplication.PERSISTENCE_UNIT);
        buildTree();
        buildMainArea();

        setSplitPosition(30);
    }

    private void buildMainArea() {
        VerticalLayout verticalLayout = new VerticalLayout();
        setSecondComponent(verticalLayout);

        personTable = new Table(null, persons);
        personTable.setSelectable(true);
        personTable.setImmediate(true);
        personTable.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                setModificationsEnabled(event.getProperty().getValue() != null);
            }

            private void setModificationsEnabled(boolean b) {
                deleteButton.setEnabled(b);
                editButton.setEnabled(b);
            }
        });

        personTable.setSizeFull();
        // personTable.setSelectable(true);
        personTable.addListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    personTable.select(event.getItemId());
                }
            }
        });

        personTable.setVisibleColumns(new Object[] { "firstName", "lastName",
                "department", "phoneNumber", "street", "city", "zipCode" });

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("Add");
        newButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                final BeanItem<Person> newPersonItem = new BeanItem<Person>(
                        new Person());
                PersonEditor personEditor = new PersonEditor(newPersonItem);
                personEditor.addListener(new EditorSavedListener() {
                    @Override
                    public void editorSaved(EditorSavedEvent event) {
                        persons.addEntity(newPersonItem.getBean());
                    }
                });
                getApplication().getMainWindow().addWindow(personEditor);
            }
        });

        deleteButton = new Button("Delete");
        deleteButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                persons.removeItem(personTable.getValue());
            }
        });
        deleteButton.setEnabled(false);

        editButton = new Button("Edit");
        editButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getApplication().getMainWindow().addWindow(
                        new PersonEditor(personTable.getItem(personTable
                                .getValue())));
            }
        });
        editButton.setEnabled(false);

        searchField = new TextField();
        searchField.setInputPrompt("Search by name");
        searchField.addListener(new TextChangeListener() {

            @Override
            public void textChange(TextChangeEvent event) {
                String text = event.getText();
                textFilter = sanitizeInputText(text);
                updateFilters();
            }

            private String sanitizeInputText(String text) {
                if (text != null) {
                    return text.replaceAll("[^a-zA-Z0-9 ]", "");
                }
                return null;
            }
        });

        toolbar.addComponent(newButton);
        toolbar.addComponent(deleteButton);
        toolbar.addComponent(editButton);
        toolbar.addComponent(searchField);
        toolbar.setWidth("100%");
        toolbar.setExpandRatio(searchField, 1);
        toolbar.setComponentAlignment(searchField, Alignment.TOP_RIGHT);

        verticalLayout.addComponent(toolbar);
        verticalLayout.addComponent(personTable);
        verticalLayout.setExpandRatio(personTable, 1);
        verticalLayout.setSizeFull();

    }

    private void buildTree() {
        groupTree = new Tree(null, departments);
        groupTree.setItemCaptionPropertyId("name");

        groupTree.setImmediate(true);
        groupTree.setSelectable(true);
        groupTree.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Object id = event.getProperty().getValue();
                if (id != null) {
                    Department entity = departments.getItem(id).getEntity();
                    departmentFilter = entity;
                } else if (departmentFilter != null) {
                    departmentFilter = null;
                }
                updateFilters();
            }

        });
        setFirstComponent(groupTree);
    }

    private void updateFilters() {
        persons.setApplyFiltersImmediately(false);
        persons.removeAllContainerFilters();
        if (departmentFilter != null) {
            // two level hierarchy at max in our demo
            if (departmentFilter.getParent() == null) {
                persons.addContainerFilter(new Equal("department.parent",
                        departmentFilter));
            } else {
                persons.addContainerFilter(new Equal("department",
                        departmentFilter));
            }
        }
        if (textFilter != null && !textFilter.equals("")) {
            Or or = new Or(new Like("firstName", textFilter + "%", false),
                    new Like("lastName", textFilter + "%", false));
            persons.addContainerFilter(or);
        }
        persons.applyFilters();
    }
}
