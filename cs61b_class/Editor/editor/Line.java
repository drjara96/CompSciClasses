package editor;

import java.util.ArrayList;


public class Line<Item> extends ArrayList<Item> {
	private double pixelSize;
    private int yPos;

	public Line() {
        super();
		pixelSize = 0.0;
	}

    protected void setPixelSize(double x) {
        pixelSize = x;
    }

    public double getPixelSize() {
        return pixelSize;
    }

    public void setYPos(int y) {
        yPos = y;
    }

    public int getYPos() {
        return yPos;
    }

}
