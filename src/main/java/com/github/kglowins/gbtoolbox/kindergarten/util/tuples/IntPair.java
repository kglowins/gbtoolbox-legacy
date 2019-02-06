package com.github.kglowins.gbtoolbox.kindergarten.util.tuples;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class IntPair {
  private int left;
  private int right;

  public static IntPair of(int left, int right) {
    return new IntPair(left, right);
  }

  public static IntPair ordered(int left, int right) {
    return (left > right) ? IntPair.of(right, left) : IntPair.of(left, right);
  }
}
