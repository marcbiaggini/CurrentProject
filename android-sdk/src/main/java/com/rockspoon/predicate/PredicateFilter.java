package com.rockspoon.predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lucas on 03/02/16.
 */
public class PredicateFilter {

  /**
   * Removes the element from removeFrom if the criteria of predicate matches
   *
   * @param removeFrom Iterable that have the items to check
   * @param predicate  The predicate with the criteria
   * @return true if the removeFrom is changed
   */
  public static <T> boolean removeIf(Iterable<T> removeFrom, Predicate<? super T> predicate) {
    return handlePredicates(removeFrom, predicate, true);
  }

  /**
   * Removes any element from removeFrom that not matches the criteria of predicate matches
   *
   * @param removeFrom Iterable that have the items to check
   * @param predicate  The predicate with the criteria
   * @return true if the removeFrom is changed
   */
  public static <T> boolean keepIf(Iterable<T> removeFrom, Predicate<? super T> predicate) {
    return handlePredicates(removeFrom, predicate, false);
  }

  private static <T> boolean handlePredicates(Iterable<T> removeFrom, Predicate<? super T> predicate, boolean remove) {
    boolean modified = false;
    Iterator<T> iterator = removeFrom.iterator();
    while (iterator.hasNext()) {
      if (remove == predicate.apply(iterator.next())) {
        iterator.remove();
        modified = true;
      }
    }
    return modified;
  }

  @SuppressWarnings("unchecked")
  private static <T> void addToPredicateList(List<Predicate<? super T>> out, Predicate<? super T> a) {
    if (a instanceof OperatorPredicate) {
      for (Object predicate: ((OperatorPredicate) a).predicates) {
        out.add((Predicate<? super T>) predicate);
      }
    } else {
      out.add(a);
    }
  }

  private static <T> Predicate<T> fillPredicateOperator(OperatorPredicate<T> out, Predicate<? super T> a, Predicate<? super T> b) {
    List<Predicate<? super T>> predicateList = new ArrayList<>();

    addToPredicateList(predicateList, a);
    addToPredicateList(predicateList, b);

    out.predicates = predicateList;
    return out;
  }

  /**
   * Creates a new Predicate with a criteria that matches a || b
   *
   * @param a first criteria
   * @param b second criteria
   * @return a new Predicate that is a || b
   */
  public static <T> Predicate<T> predicateOr(Predicate<? super T> a, Predicate<? super T> b) {
    return fillPredicateOperator(new OrPredicate<>(), a, b);
  }

  /**
   * Creates a new Predicate with a criteria that matches a && b
   *
   * @param a first criteria
   * @param b second criteria
   * @return a new Predicate that is a && b
   */
  public static <T> Predicate<T> predicateAnd(Predicate<? super T> a, Predicate<? super T> b) {
    return fillPredicateOperator(new AndPredicate<>(), a, b);
  }

  private abstract static class OperatorPredicate<T> implements Predicate<T> {
    protected Iterable<? extends Predicate<? super T>> predicates;

    protected OperatorPredicate() {
      this.predicates = new ArrayList<>();
    }

    protected OperatorPredicate(Iterable<? extends Predicate<? super T>> predicates) {
      this.predicates = predicates;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof OrPredicate) {
        OrPredicate<?> b = (OrPredicate<?>) obj;
        Iterator<?> iterator1 = predicates.iterator();
        Iterator<?> iterator2 = b.predicates.iterator();
        while (iterator1.hasNext()) {
          if (!iterator2.hasNext() || !iterator1.next().equals(iterator2.next())) {
            return false;
          }
        }
        return !iterator2.hasNext();
      }
      return false;
    }
  }

  private static class OrPredicate<T> extends OperatorPredicate<T> {

    protected OrPredicate() {
    }

    protected OrPredicate(Iterable<? extends Predicate<? super T>> predicates) {
      super(predicates);
    }

    @Override
    public boolean apply(T t) {
      for (Predicate<? super T> predicate: predicates) {
        if (predicate.apply(t)) {
          return true;
        }
      }
      return false;
    }
  }

  private static class AndPredicate<T> extends OperatorPredicate<T> {

    protected AndPredicate() {
    }

    private AndPredicate(Iterable<? extends Predicate<? super T>> predicates) {
      super(predicates);
    }

    @Override
    public boolean apply(T t) {
      for (Predicate<? super T> predicate: predicates) {
        if (!predicate.apply(t)) {
          return false;
        }
      }
      return true;
    }
  }
}
