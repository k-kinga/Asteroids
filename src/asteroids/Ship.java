package asteroids;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Kinga
 */
public class Ship extends Character{

    public Ship(int x, int y){
        super(new Polygon(0, 15, 0, 0, 17, 7.5),x,y);
    }

}