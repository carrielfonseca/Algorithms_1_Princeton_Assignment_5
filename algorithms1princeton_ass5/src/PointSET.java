


import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fabio
 */
public class PointSET {
    private SET<Point2D> points = new SET<Point2D>();    
    
    public PointSET() {     
    }
    
    public boolean isEmpty()  {
        return points.isEmpty();
    }
    
    public  int size()  {
        return points.size();
    }
    
    public  void insert(Point2D p) {
        points.add(p);        
    }
    
    public  boolean contains(Point2D p)    {
        return points.contains(p);
    }
    
    public  void draw()   {
       StdDraw.setPenColor(StdDraw.BLACK);
       StdDraw.setPenRadius(0.01);
       for (Point2D p: points) {
          StdDraw.point(p.x(), p.y());
       }      
    }
    
    public Iterable<Point2D> range(RectHV rect)  {    // all points that are inside the rectangle 
        LinkedQueue<Point2D> pointsInTheRectangle = new LinkedQueue<Point2D>(); 
        for (Point2D p: points) {
          if (rect.contains(p)) {
             pointsInTheRectangle.enqueue(p);
          }
        }  
        return pointsInTheRectangle;        
    }
    
    public Point2D nearest(Point2D p)    {    // a nearest neighbor in the set to point p; null if the set is empty 
        if (isEmpty()) {
            return null;
        }
        Point2D pointNearest = points.min(); // just to have a starting point (is there a better way to do it ?)
        for (Point2D p2: points) {
           if (p2.distanceTo(p) < pointNearest.distanceTo(p)) {
               pointNearest = p2;
           }
        }  
        return pointNearest;        
    }
    
    public static void main(String[] args)       {
        PointSET ps = new PointSET();
        Point2D[] pArray = new Point2D[4];

       
/*
        pArray[0] = new Point2D(5, 7);
        pArray[1] = new Point2D(10, 10);
        pArray[2] = new Point2D(1, 2);
        pArray[3] = new Point2D(4, 3);
        */
        
       pArray[0] = new Point2D(9, 9);
        pArray[1] = new Point2D(5, 6);
        pArray[2] = new Point2D(1, 2);
        pArray[3] = new Point2D(3, 4);

        ps.insert(pArray[0]);
        ps.insert(pArray[1]);
        ps.insert(pArray[2]);
        ps.insert(pArray[3]);
      
     
        System.out.println(ps.nearest(new Point2D(5,5))) ;
        
   
        
        
    }
}
