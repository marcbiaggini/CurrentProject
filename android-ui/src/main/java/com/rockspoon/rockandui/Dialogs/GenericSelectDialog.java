package com.rockspoon.rockandui.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 17/11/15.
 */
public class GenericSelectDialog extends AbstractTitleMessageDialog<GenericSelectDialog> {

  public static String PARAM_TITLE = "title";
  public static String PARAM_MESSAGE = "message";
  public static String PARAM_CANCELABLE = "cancelable";

  private ListView listView;

  private ListAdapter viewAdapter;
  private AdapterView.OnItemClickListener listViewItemClick;

  public GenericSelectDialog() {
    setStyle(STYLE_NO_FRAME, R.style.rsDialogExitDown);
  }

  /**
   * Create a GenericSelectDialog using bundle arguments.<BR/>
   * <BR/>
   * Bundle Arguments:<BR/>
   * title - Dialog Title String - This will set the title of dialog. Default: Please Wait
   * message - Dialog Content Message String. Default: Nothing
   * progressIndeterminate - Dialog Progress Indeterminate Boolean. -- This will set Progress Bar as Determinate. Default: False
   * cancelable - Dialog Cancelable -- This will set if this dialog is cancelable. Default: False
   *
   * @param args Bundle Arguments
   * @return New GenericSelectDialog
   */
  public static GenericSelectDialog newInstance(final Bundle args) {
    final GenericSelectDialog dialog = new GenericSelectDialog();
    dialog.setArguments(args);
    return dialog;
  }

  /**
   * Create a GenericSelectDialog using bundle arguments.<BR/>
   * @return New GenericSelectDialog
   */
  public static GenericSelectDialog newInstance() {
    return new GenericSelectDialog();
  }

  @Override
  public void setArguments(Bundle args) {
    if (args != null) {
      setTitle(args.getString(PARAM_TITLE));
      setMessage(args.getString(PARAM_MESSAGE));
      setCancelable(args.getBoolean(PARAM_CANCELABLE, false));
    }
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View v = inflater.inflate(R.layout.dialog_generic_select, container, false);
    titleView = (TextView) v.findViewById(R.id.dialog_generic_select_title);
    messageView = (TextView) v.findViewById(R.id.dialog_generic_select_message);
    listView = (ListView) v.findViewById(R.id.dialog_generic_select_list);

    setTitle(title);
    setMessage(message);
    setAdapter(viewAdapter);
    setOnItemClickListener(listViewItemClick);

    return v;
  }

  /**
   * Sets the adapter for the listview
   *
   * @param adapter ListAdapter
   * @return this dialog
   */
  public GenericSelectDialog setAdapter(final ListAdapter adapter) {
    this.viewAdapter = adapter;

    if (listView != null)
      listView.setAdapter(adapter);

    return this;
  }

  /**
   * Sets the List View Item Click Listener
   *
   * @param clickListener AdapterView.OnItemClickListener
   * @return this dialog
   */
  public GenericSelectDialog setOnItemClickListener(final AdapterView.OnItemClickListener clickListener) {
    this.listViewItemClick = clickListener;

    if (listView != null)
      listView.setOnItemClickListener(clickListener);

    return this;
  }

  @Override
  public GenericSelectDialog getThis() {
    return this;
  }
}
