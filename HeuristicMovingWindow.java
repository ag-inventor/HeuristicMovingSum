import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;

public class HeuristicMovingWindow {

    double optimisedSum;
    double slope;
    int window, count;

    public HeuristicMovingWindow(int window) {
        this.window = window;
        slope = 0.0;
        optimisedSum = 0.0;
        count = 0;
    }

    /*

    //first w samples, where w is window size, should not be NaNs
     */

    public boolean addNewDataSample(double d){

        if(count < window){
            if(Double.isNaN(d)) {
                return false;

            }
            if(count > 0) {
                slope += d - optimisedSum/count;
            }
            optimisedSum += d;
            ++count;

        }
        else{
            if(Double.isNaN(d)){
                d = optimisedSum/window;
            }
            double avg = optimisedSum/window;
            double last_sample = avg - slope/window;
            
            slope += d - avg;
            slope -= d - last_sample;

            optimisedSum -= last_sample;
            optimisedSum += d;
        }
        return true;

    }

    public double getMovingSum(){
        return optimisedSum;
    }

    /*
    Testing Code

     */
    public static void compareHeuristicWithRealData(double[] arr, int window, String file, HeuristicMovingWindow h_m_avg) throws IOException {
        double runningSum = arr[0];
        BufferedWriter bw = new BufferedWriter((new FileWriter(file)));
        bw.write("Index, Actual Avg, Aprrox. Avg, Deviation, Error Percentage, d(x), dd(x), sample, estimated\n");
        double slope = 0.0;
        for(int i = 1 ; i < window; i++){
            runningSum += arr[i];
            h_m_avg.addNewDataSample(arr[i]);
            slope += arr[i] - arr[i-1];
        }
        double heuristicSum = h_m_avg.getMovingSum();
        bw.write("0,"+runningSum+","+heuristicSum+",0.0,0.0,0.0,0.0,0.0,"+arr[window-1]+","+arr[window-1]+"\n");



        for(int i = window; i<arr.length; ++i){

            h_m_avg.addNewDataSample(arr[i]);

            runningSum -= arr[i-window];
            runningSum += arr[i];

            heuristicSum = h_m_avg.getMovingSum();

            double diff = Math.abs(runningSum - heuristicSum);
            double perc = 100*diff/runningSum;
            double dx = arr[i] - arr[i-1];
            double ddx = arr[i] + arr[i-2] - 2*arr[i-1];

            bw.write((i-window+1)+","+runningSum+","+heuristicSum+","+diff+","+perc+","+dx+","+ddx+","+ arr[i]+",0.0"+"\n");

        }
        bw.close();
    }

    public static void main(String[] args) throws Exception {
        //String path = "/Users/aditgoel/Downloads/10245_2/cprod-blue-snbservice-prod_Overall_Overall_AllTransactions.csv";
        String path = "/Users/aditgoel/Downloads/10108_18/10108_18/1.csv";
        BufferedReader br = new BufferedReader(new FileReader(path));
        double[] arr = from_file(br,2, 2600); // fill this using file

        double[] ushape =arr;

        String avgFilePath = "/Users/aditgoel/Documents/CSVOutput/heuristic.csv";
        int window = 6;
        HeuristicMovingWindow h_m_avg = new HeuristicMovingWindow(window);

        compareHeuristicWithRealData(ushape, window, avgFilePath, h_m_avg);

        //double[] wave = wc.transform(arr);
        //String file = "/Users/aditgoel/Documents/CSVOutput/wave1.csv";
        //BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        //write(bw, arr, wave);

    }

    public static void write(BufferedWriter bw, double[] data, double[] wave) throws IOException{

        bw.write("S No., data, wave\r\n");
        for(int i = 0 ; i< data.length; ++i){

            bw.write((i+1)+ "," + data[i]+","+ wave[i]+"\r\n" );
        }
        bw.close();

    }


    public static double[] from_file(BufferedReader br, int index, int n) throws IOException {

        int count = 0;
        double[] data = new double[n];


        String line;

        while ((line = br.readLine()) != null && count < n) {
            //System.out.println(line);
            String[] values = line.split(",");
            data[count++] = Double.parseDouble(values[index]);

        }
        br.close();

        if(count < n){
            double[] trimmed = new double[count];
            for(int i = 0 ; i < count; i++){
                trimmed[i] = data[i];
            }
            return trimmed;

        }

        return data;

    }

}
