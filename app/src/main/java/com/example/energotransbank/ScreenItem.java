package com.example.energotransbank;

public class ScreenItem {
    String NameBoard,DescriptionBoard, Skip;
    int ImageBoard, ImagePlus;

    public ScreenItem(String nameBoard, String descriptionBoard, String skip, int imageBoard, int imagePlus) {
        NameBoard = nameBoard;
        DescriptionBoard = descriptionBoard;
        Skip = skip;
        ImageBoard = imageBoard;
        ImagePlus = imagePlus;
    }

    public void setNameBoard(String nameBoard) {
        NameBoard = nameBoard;
    }

    public void setDescriptionBoard(String descriptionBoard) {
        DescriptionBoard = descriptionBoard;
    }

    public void setSkip(String skip) {
        Skip = skip;
    }

    public void setImageBoard(int imageBoard) {
        ImageBoard = imageBoard;
    }

    public void setImagePlus(int imagePlus) {
        ImagePlus = imagePlus;
    }

    public String getNameBoard() {
        return NameBoard;
    }

    public String getDescriptionBoard() {
        return DescriptionBoard;
    }

    public String getSkip() {
        return Skip;
    }

    public int getImageBoard() {
        return ImageBoard;
    }

    public int getImagePlus() {
        return ImagePlus;
    }
}
