package com.wire.bots.hello;

/**
 *
 * @author Propeller.ai
 * http://propeller.rocks
 */
public class ImageClassificationRequest {
    
    private byte[] image;

    public ImageClassificationRequest() {
        //empty
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {

        return image;
    }
}
