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

    public Float[] getConfidences() {
        return confidences;
    }
}
