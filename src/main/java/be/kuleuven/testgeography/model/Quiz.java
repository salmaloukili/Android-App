package be.kuleuven.testgeography.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import be.kuleuven.testgeography.R;
import be.kuleuven.testgeography.activities.MainActivity;

public class Quiz implements Parcelable {
    private String username;
    private String types;// example of format: type1|type2|type3
    private String regions;
    private int nrOfQuestions;
    private ArrayList<Question> answers;
    private int currentNr;// at which question are we in the quiz
    private int score;
    private ArrayList<Integer> idAnswers;
    private boolean QCM;
    private ArrayList<String> flagURL = new ArrayList<String>();

    public Quiz() {
        this.username = "Dummy";
        this.nrOfQuestions = 10; // minimum amount of questions
        answers = new ArrayList<>();
        regions = "";
        types = "";
        currentNr = 0;
        score = 0;
    }

    public Quiz(JSONObject o) {
        try {
            username = o.getString("user");
            String typeString = o.getString("types");
            if (typeString.toLowerCase().contains("all")) {
                types = ""; // we don't need to filter based on quiz questions
            } else{
                types = typeString;
            }
            // parse the string 'types' to get the different types
            String regionString = o.getString("regions");
            if (regionString.toLowerCase().contains("all")) {
                regions = ""; // we don't need to filter based on regions
            } else{
                regions = regionString;
            }
            // parse the string 'regions' to get the different regions
            nrOfQuestions = o.getInt("questions");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    protected Quiz(Parcel in) {
        username = in.readString();
        types = in.readString();
        regions = in.readString();
        nrOfQuestions = in.readInt();
        answers = in.readArrayList(null);
        currentNr = in.readInt();
        score = in.readInt();
        //idAnswers = in.createTypedArrayList();
    }

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

    public int getNrOfQuestions(){
        return nrOfQuestions;
    }

    public void setNrOfQuestions(int nr) {
        this.nrOfQuestions = nr;
    }

    public ArrayList<Question> getAnswers(){
        return answers;
    }

    public int getCurrentNr(){
        return currentNr;
    }

    public int getScore(){
        return score;
    }

    public ArrayList<String> getFlagURL(){return flagURL;}


    // Returns the AnswerQuiz object at which the quiz is currently at (stored at int 'currentNr');
    public Question getCurrentQuestion(){
        return getAnswer(currentNr);
    }

    public String getCurrentQuestionText(){
        Question q = getCurrentQuestion();
        return q.makeQuestionString(q.getDescriptionQuestion());
    }

    public int incrementScore(){
        score++;
        return score;
    }

    // Returns the quiz types as a String[]
    public String[] getQuizTypesArray(){
        if (types.contains("_")){
            String[] typesList = types.split("_");
            return types.split("_");
        }
        return new String[]{types};
    }

    // Returns the quiz regions as a String[]
    public String[] getQuizRegionsArray(){
        if (regions.contains("_")){
            return regions.split("_");
        }
        return new String[]{regions};
    }

    // Add a quiz type to the list of selected quiz types
    public void addType(String type) {
        if (!types.contains(type)) {
            if (types.length() != 0) {
                types = types + "_" + type;
            } else{
                types = type;
            }
        }
    }

    // Add a region to the list of selected regions
    public void addRegion(String region) {
        if (!regions.contains(region)) {
            //quizRegions.add(region);
            if (regions.length() != 0){
                regions = regions + "_" + region;
            } else{
                regions = region;
            }
        }
    }

    // Returns the AnswerQuiz object stored at the index 'index' in answers
    public Question getAnswer(int index) {
        if (index >= answers.size()){
            return null;
        }
        Question ans = answers.get(index);
        return ans;
    }

    /** Sets an multiple choice answer in the ArrayList answers at the given index
     * @param index = index in the ArrayList answers
     * @param answer = description of the answer (e.g.: if the question type is to guess a country, a description could be "Belgium"
     * @param nrOfOptions = the number of options for the multiple choice question
     * @param question = description of the question (e.g.: if the question type is to guess a country or a flag based on the capital, question could be "Brussels")
     */
    public void setAnswer(int index, String answer, int nrOfOptions, String question){
        if (getAnswer(index) == null){
            // create a new question
            answers.add(new MCQuestion(getRandomType(),answer,nrOfOptions,question));
        } else{
            answers.get(index).setDescriptionAnswer(answer);
            answers.get(index).setDescriptionQuestion(question);
        }
    }

    /**
     * @param index = index in the ArrayList answers --> the question for which the options will be set
     * @param options = a list of Strings which are the options of answers in a multiple choice question
     */
    public void setOptions(int index, String[] options){
        int i = 0;
        for (String op:options) {
            ((MCQuestion) answers.get(index)).setOption(op,i);
            //answer.setOption(op, i);
            i++;
        }
    }

    /**
     * @param index = index in the ArrayList answers --> the question from which the options will be retrieved
     * @return a list of Strings which are the options of answers in the specified (by index) question
     */
    public String[] getOptions(int index){
        MCQuestion answer = (MCQuestion) answers.get(index);
        return answer.getOptionDescriptions();
    }

    public String[] shuffleOptions(String[] options){
        int size = options.length;
        Random rand = new Random();
        for (int i = 0; i < size; i++){
            int index = rand.nextInt(size);
            String selected = options[index];
            options[index] = options[i];
            options[i] = selected;
        }
        return options;
    }

    public String getRandomType(){
        String[] typeList = getQuizTypesArray();
        if (typeList.length == 1){
            return typeList[0];// only one type is selected
        }
        Random random = new Random();
        int index = random.nextInt(typeList.length);
        return (typeList[index]);
    }

    public void setRandomTypes(int nrOfOptions) {
        String type;
        if (nrOfOptions == 1){
            for (int i = 0; i < nrOfQuestions; i++){
                type = getRandomType();
                answers.add(new Question(type, "", ""));
            }
        } else{
            for (int i = 0; i < nrOfQuestions; i++) {
                // generate random index at which we will get a randomly chosen type from typeList
                type = getRandomType();
                // add a new ("empty") question to the list of questions
                answers.add(new MCQuestion(type, "", nrOfOptions, ""));
            }
        }
    }

    public void setAnswers(ArrayList<String> countries, ArrayList<String> capitals, ArrayList<String> flags, ArrayList<String> abbreviations){
        // Get random and unique indices into the arrayList idAnswers which is a list that contains the index for each question
        idAnswers = new ArrayList<Integer>();
        Random rand = new Random();
        int maxSize = countries.size();
        int random_index = rand.nextInt(maxSize);
        for (int i = 0; i < nrOfQuestions; i++){
            while (idAnswers.contains(random_index)) {
                random_index = rand.nextInt(maxSize);
            }
            idAnswers.add(random_index);
            flagURL.add("");
        }
        // Loop through the questions
        int n = 0;
        int index;// initialize index. This variable represents an index for the arraylists
        for (Question q : answers){
            index = idAnswers.get(n);// get the index for the correct answer/question from the arraylist idAnswers
            switch(q.getType()){
                case "Guess the country based on the flag":
                    q.setupQuestion(flags, countries, index);
                    flagURL.set(n, flags.get(index));
                    break;
                /*case "Guess the flag based on the country":
                    q.setupQuestion(countries, flags, index);
                    break;*/
                case "Guess the country based on the capital":
                    q.setupQuestion(capitals, countries, index);
                    break;
                case "Guess the capital based on the country":
                    q.setupQuestion(countries, capitals, index);
                    break;
                case "Guess the country based on the abbreviation":
                    q.setupQuestion(abbreviations, countries, index);
                    break;
                case "Guess the abbreviation based on the country":
                    q.setupQuestion(countries, abbreviations, index);
                    break;
            }
            n++;
        }
    }

    public void nextQuestion(){
        currentNr++;

    }

    public void resetQuestions(){
        answers.clear();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        //parcel.writeList(quizTypes);
        parcel.writeString(types);
        //parcel.writeList(quizRegions);
        parcel.writeString(regions);
        parcel.writeInt(nrOfQuestions);
        parcel.writeList(answers);
        parcel.writeInt(currentNr);
        parcel.writeInt(score);
    }

    public void readFromParcel(Parcel in){
        this.username = in.readString();
        //in.readStringList(quizTypes);
        this.types = in.readString();
        //in.readStringList(quizRegions);
        this.regions = in.readString();
        this.nrOfQuestions = in.readInt();
        in.readTypedList(answers, Question.CREATOR);
        this.currentNr = in.readInt();
        this.score = in.readInt();
    }
}
