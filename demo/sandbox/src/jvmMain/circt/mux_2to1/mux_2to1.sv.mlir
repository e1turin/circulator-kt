module {
  hw.module @mux_2to1(in %a : i3, in %b : i4, in %sel : i2, out out : i3) {
    %c2_i3 = hw.constant 2 : i3
    %0 = comb.extract %sel from 0 : (i2) -> i1
    %1 = comb.extract %b from 1 : (i4) -> i3
    %2 = comb.extract %sel from 1 : (i2) -> i1
    %3 = comb.mux %2, %a, %c2_i3 : i3
    %4 = comb.mux %0, %1, %3 : i3
    hw.output %4 : i3
  }
}
