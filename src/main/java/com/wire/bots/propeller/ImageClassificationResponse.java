package com.wire.bots.propeller;

/**
 *
 * @author Propeller.ai
 * http://propeller.rocks
 */
public class ImageClassificationResponse {
    
    private  String[] categories;
    private  Float[] confidences;


    public ImageClassificationResponse(){
        //empty
    }

    public ImageClassificationResponse(String[] categories, Float[] confidences) {
        this.categories = categories;
        this.confidences = confidences;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getConfidences() {
        String category = "No classification results";
        if (confidences != null && confidences.length > 0) {
            Float largest = confidences[0];
            int index = 0;
            for (int i = 1; i < confidences.length; i++) {
                    if (confidences[i] > largest) {
                            largest = confidences[i];
                            index = i;
                    }
            }
            
            category = getCategories()[index];
        }
         
        return category;
    }
}
