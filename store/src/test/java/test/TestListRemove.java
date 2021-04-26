package test;

import spes.struct.LinkedList;
import spes.struct.List;

public class TestListRemove {
    static class Sample{
        public int val;

        public Sample(int val) {
            this.val = val;
        }
    }
    public static void showList(List<Sample> smps){
        System.out.println("data: ");
        for(Sample s : smps)
            System.out.print(s.val + " ");
        System.out.println();
    }
    public static void main(String[] args){
        List<Sample> smps = new LinkedList<>();
        for(int i = 0; i < 10; ++i)
            smps.add(new Sample(i));
        showList(smps);
        smps.remove(smps.get(3));
        smps.remove(smps.get(7));
        showList(smps);
    }
}
