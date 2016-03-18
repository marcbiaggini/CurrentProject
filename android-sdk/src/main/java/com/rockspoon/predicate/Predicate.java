package com.rockspoon.predicate;

/**
 * Created by lucas on 03/02/16.
 */
public interface Predicate<T> {
  boolean apply(T input);

  boolean equals(Object obj);
}
