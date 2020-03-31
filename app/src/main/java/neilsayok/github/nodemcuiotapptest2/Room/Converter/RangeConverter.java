package neilsayok.github.nodemcuiotapptest2.Room.Converter;

public class RangeConverter {

    public static int convertRange(int value,int OldMax,int OldMin,int NewMax,int NewMin){

        int OldRange = (OldMax - OldMin);
        if (OldRange == 0){
            return NewMin;
        }
        else
        {
            int NewRange = (NewMax - NewMin)  ;
            return (((value - OldMin) * NewRange) / OldRange) + NewMin;
        }
    }


}
