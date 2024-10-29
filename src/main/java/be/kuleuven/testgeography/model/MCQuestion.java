package be.kuleuven.testgeography.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Random;

public class MCQuestion extends Question implements Parcelable {
    private String[] optionDescriptions;

    MCQuestion(String type, String answer, int nrOfOptions, String question){
        super(type, answer, question);
        optionDescriptions = new String[nrOfOptions];
    }

    protected MCQuestion(Parcel in) {
        super(in);
        optionDescriptions = in.createStringArray();
    }

    public static final Creator<MCQuestion> CREATOR = new Creator<MCQuestion>() {
        @Override
        public MCQuestion createFromParcel(Parcel in) {
            return new MCQuestion(in);
        }

        @Override
        public MCQuestion[] newArray(int size) {
            return new MCQuestion[size];
        }
    };

    public void setOption(String option, int index){
        optionDescriptions[index] = option;
    }

    public String[] getOptionDescriptions(){
        return optionDescriptions;
    }

    public String[] shuffleOptions(){
        int size = optionDescriptions.length;
        Random rand = new Random();
        for (int i = 0; i < size; i++){
            int index = rand.nextInt(size);
            String selected = optionDescriptions[index];
            optionDescriptions[index] = optionDescriptions[i];
            optionDescriptions[i] = selected;
        }
        return optionDescriptions;
    }

    public void setupOptions(ArrayList<String> answerDescriptions, int questionIndex){
        ArrayList<Integer> indicesOptions = new ArrayList<Integer>();
        setOption(answerDescriptions.get(questionIndex),0);
        Random rand = new Random();
        int size = answerDescriptions.size();
        int indexOption = rand.nextInt(size);
        boolean again;
        for (int i = 1; i < optionDescriptions.length; i++) {
            again = false;
            int tries = 0;
            while (indexOption == questionIndex || indicesOptions.contains(indexOption) || again) {
                indexOption = rand.nextInt(size);
                if (type.contentEquals("Guess the abbreviation based on the country")) {
                    if (!checkSimilarityAbbreviation(answerDescriptions.get(questionIndex), answerDescriptions.get(indexOption),1)){
                        tries++;
                        if (tries > answerDescriptions.size() - 5) {
                            again = false;
                        } else{
                            again = true;
                        }
                    }
                }
            }
            setOption(answerDescriptions.get(indexOption), i);
            indicesOptions.add(indexOption);
        }
        shuffleOptions();
    }

    @Override
    public void setupQuestion(ArrayList<String> questionDescriptions, ArrayList<String> answerDescriptions, int questionIndex){
        setDescriptionAnswer(answerDescriptions.get(questionIndex));
        setDescriptionQuestion(questionDescriptions.get(questionIndex));
        setupOptions(answerDescriptions, questionIndex);
    }

    @Override
    public boolean isMCQuestion(){
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeStringArray(optionDescriptions);
    }

    public boolean checkSimilarityAbbreviation(String word1, String word2, int nrOfSameChars){
        if (nrOfSameChars <= 0){
            return true;
        }
        char[] lettersWord1 = word1.toCharArray();
        char[] lettersWord2 = word2.toCharArray();
        int nr = 0;
        for (char c1: lettersWord1){
            for (char c2 : lettersWord2){
                if (c1 == c2){
                    nr++;
                    if (nr == nrOfSameChars){
                        return true;
                    }
                }
            }
        }
        if (nr >= nrOfSameChars){
            return true;
        }
        return false;
    }
}
