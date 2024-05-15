package com.teclever.dfcc.utils;

import javafx.collections.ObservableList;

public interface TableViewFactory<T> {
	 CustomTableView<T> createTableView(ObservableList<T> items, boolean addUserColumn, boolean addCheckboxColumn);
}