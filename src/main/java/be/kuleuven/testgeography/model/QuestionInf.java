package be.kuleuven.testgeography.model;

import java.util.ArrayList;

public interface QuestionInf {

    /**
     * Getter method for type attribute
     * @return type of question
     */
    public String getType();

    /**
     * Setter method for type attribute
     * @param type: the type of question (e.g.: Q:Capital, A:Country)
     */
    public void setType(String type);

    /**
     * Getter method for the descriptionAnswer attribute
     * @return
     */
    public String getDescriptionAnswer();

    /**
     *
     * @param nameAnswer
     */
    public void setDescriptionAnswer(String nameAnswer);

    /**
     *
     * @return
     */
    public String getDescriptionQuestion();

    /**
     *
     * @param nameQuestion
     */
    public void setDescriptionQuestion(String nameQuestion);


    /** makeQuestion checks what the question type is
     *
     * @param q = the question description (e.g.: If the question would be to guess a county based on the capital, q could be "Brussels")
     * @return the string that will be shown at the top of the quiz page (e.g.: "Brussels is the capital of ...?")
     */
    public String makeQuestionString(String q);

    public void setupQuestion(ArrayList<String> questionDescriptions, ArrayList<String> answerDescriptions, int questionIndex);

    /**
     *
     * @return true if Question object is of MCQuestion child class or false if the object is of Question parent class
     */
    public boolean isMCQuestion();

}

