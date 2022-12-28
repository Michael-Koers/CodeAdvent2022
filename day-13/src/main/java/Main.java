import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        String filePath = "src/main/resources/input.txt";
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        List<Object> Json = parseJson(lines);

        int result_puzzle1 = 0;

        for (int i = 0; i < Json.size(); i+=2) {
            int compare = compare(Json.get(i), Json.get(i+1));
            if (compare == -1) {
                result_puzzle1 += (i/2) + 1;
            }
        }

        System.out.printf("Puzzle 1 result: %s%n", result_puzzle1);

        // Add divider signal
        List<List<Double>> dividerPacket2 = new ArrayList<>(){{
            add(new ArrayList<>(){{
                add(2d);
            }});
        }};
        List<List<Double>> dividerPacket6 = new ArrayList<>(){{
            add(new ArrayList<>(){{
                add(6d);
            }});
        }};

        Json.add(dividerPacket2);
        Json.add(dividerPacket6);

        Json.sort(main::compare);

        int index2 = 0;
        int index6 = 0;
        for(int i = 0; i<Json.size(); i++){
            if(Json.get(i).toString().equals("[[2.0]]")){
                index2 = i+1;
            } else if(Json.get(i).toString().equals("[[6.0]]")){
                index6 = i+1;
            }
        }

        System.out.printf("Puzzle 2 result: %s%n", (index2 * index6));
    }

    public static int compare(Object leftPacket, Object rightPacket) {

        if (leftPacket instanceof Double l && rightPacket instanceof Double r) {
            return Double.compare(l, r);
        }

        if (leftPacket instanceof ArrayList<?> l && rightPacket instanceof ArrayList<?> r) {
            int smallestArray = Math.min(l.size(), r.size());
            for (int i = 0; i < smallestArray; i++) {
                int result = compare(l.get(i), r.get(i));
                if (result != 0) {
                    return result;
                }
            }
            // In case values in arrays where the same
            return Integer.compare(l.size(), r.size());
        }

        List<Object> temp = new ArrayList<>();
        if (leftPacket instanceof ArrayList<?>) {
            temp.add(rightPacket);
            return compare(leftPacket, temp);
        } else{
            temp.add(leftPacket);
            return compare(temp, rightPacket);
        }
    }


    public static List<Object> parseJson(List<String> lines) {

        Gson gson = new Gson();
        List<Object> data = new ArrayList<>();

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            data.add(gson.fromJson(line, ArrayList.class));
        }

        return data;
    }
}
