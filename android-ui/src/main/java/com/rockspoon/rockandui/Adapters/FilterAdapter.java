package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.widget.BaseAdapter;

import com.rockspoon.predicate.PredicateFilter;
import com.rockspoon.predicate.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lucas on 07/01/16.
 */
public abstract class FilterAdapter<T> extends BaseAdapter {

  protected List<T> originalList = new ArrayList<>();
  protected List<T> filteredList = new ArrayList<>();
  protected Predicate<T> filter = (input) -> false;

  protected Comparator<T> orderByComparator;

  protected Context ctx;

  public FilterAdapter(final Context ctx) {
    this.ctx = ctx;
  }

  public FilterAdapter(final Context ctx, final List<T> data) {
    this.ctx = ctx;
    originalList.clear();
    originalList.addAll(data);
    filteredList.clear();
    filteredList.addAll(data);
    refresh();
  }

  @Override
  public int getCount() {
    return filteredList.size();
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  public void setData(final List<T> data) {
    originalList.clear();
    originalList.addAll(data);
    filteredList.clear();
    filteredList.addAll(data);
    refresh();
  }

  public List<T> getItems() {
    return filteredList;
  }

  @Override
  public T getItem(int position) {
    return filteredList.get(position);
  }

  public FilterAdapter<T> resetFilter() {
    filteredList.clear();
    filteredList.addAll(originalList);
    filter = (input) -> false;
    orderByComparator = null;
    return this;
  }

  public FilterAdapter<T> removeIf(final Predicate<T> filter) {
    this.filter = PredicateFilter.predicateOr(filter, this.filter);
    return this;
  }

  public FilterAdapter<T> refresh() {
    PredicateFilter.removeIf(filteredList, filter);

    if (orderByComparator != null) {
      Collections.sort(filteredList, orderByComparator);
    }

    this.notifyDataSetChanged();
    return this;
  }

  public FilterAdapter<T> orderBy(Comparator<T> order, final boolean reverse) {
    orderByComparator = reverse ? Collections.reverseOrder(order) : order;
    return this;
  }

  public FilterAdapter<T> orderBy(Comparator<T> order) {
    orderByComparator = order;
    return this;
  }

}
