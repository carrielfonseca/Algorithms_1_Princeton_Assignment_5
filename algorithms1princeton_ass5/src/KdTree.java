

import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fabio
 * Check later: how to make the method nearest more elegant when checking the Node to inspect first?
 * v4: implements the drawing method
 */

public class KdTree {
    private Node root;             // root of BST
    private int size = 0;         // don~t need to have the sie of each Node, since we don't do operation as rank()
    private final RectHV GRID = new RectHV(0,0,1,1);
    
    private static class  Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        private boolean isVertical;
        
        public Node(Point2D p, boolean isVertical, RectHV rect) {
            this.p = p;
            this.isVertical = isVertical;
            this.rect = rect;
        }
    }      
    
    public KdTree() {     
    }
    
    public boolean isEmpty()  {
        return size() == 0;
    }
    
    public  int size()  {
        return size;
    }
    
        
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("first argument to insert() is null");
        root = insert(root, p, true, GRID); //starts with the vertical separation
       // size++;
    }
    
    private Node insert(Node x, Point2D p, boolean isVertical, RectHV rect) {
        if (x == null) {
            size = size + 1;
            return new Node(p, isVertical, rect);
        }
        
        //if (x.p.equals(p)) return x;
        if(x.p.compareTo(p) == 0) return x;
        int cmp;
        if (x.isVertical) {
           if (p.x() < x.p.x()) {  // maybe a bit confusing. First x is the Node, second x is the axis
              cmp = - 1;
              rect = new RectHV(x.rect.xmin(),x.rect.ymin(), x.p.x(), x.rect.ymax());
           }
           else if (p.x() > x.p.x()) {
               cmp = 1;
               rect = new RectHV(x.p.x(), x.rect.ymin(), x.rect.xmax(), x.rect.ymax());               
           }
           else {
               cmp = 0;
               rect = new RectHV(x.rect.xmin(),x.rect.ymin(), x.p.x(), x.rect.ymax());
           }
        }
        else {
            if (p.y() < x.p.y()) {  // maybe a bit confusing. First x is the Node, second x is the axis
              cmp = - 1;
              rect = new RectHV(x.rect.xmin(), x.rect.ymin(), x.rect.xmax(), x.p.y());
           }
           else if (p.y() > x.p.y()) {
               cmp = 1;
               rect = new RectHV(x.rect.xmin(), x.p.y(), x.rect.xmax(), x.rect.ymax());
           }
           else {
               cmp = 0;
               rect = new RectHV(x.rect.xmin(), x.rect.ymin(), x.rect.xmax(), x.p.y());
               
               
           }            
        } 
        if (cmp < 0)      x.lb = insert(x.lb, p, !x.isVertical, rect);
        else if (cmp > 0) x.rt = insert(x.rt, p, !x.isVertical, rect);
        else              x.lb = insert(x.lb, p, !x.isVertical, rect);
        return x;        
    }
    
    
    
    public  boolean contains(Point2D p)    {
        if (p == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(p) != null;
    }
    
       
    private Point2D get(Point2D p) {
        return get(root, p);
    }

    private Point2D get(Node x, Point2D p) {
        if (x == null) return null;
        //if (x.p.equals(p)) return p;
        if(x.p.compareTo(p) ==0) return p;
        
        int cmp; 
        if (x.isVertical) {
           if (p.x() < x.p.x()) {  // maybe a bit confusing. First x is the Node, second x is the axis
              cmp = - 1;
           }
           else if (p.x() > x.p.x()) {
               cmp = 1;
           }
           else {
               cmp = 0;
           }
        }
        else {
            if (p.y() < x.p.y()) {  // maybe a bit confusing. First x is the Node, second x is the axis
              cmp = - 1;
           }
           else if (p.y() > x.p.y()) {
               cmp = 1;
           }
           else {
               cmp = 0;
           }            
        } 
        if      (cmp < 0) return get(x.lb, p);
        else if (cmp > 0) return get(x.rt, p);
        else              return get(x.lb, p);    // ties go to the left or bottom
    }
    
    
    
    public  void draw()   {
     // just to fullfil the API  
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.005);
        root.rect.draw();
        draw(root);
    }
    
    private void draw(Node x) { 
        if (x == null) return;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(x.p.x(), x.p.y());
        StdDraw.setPenRadius(0.005);      
        
        if(x.isVertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(x.p.x(), x.rect.ymin(), x.p.x(), x.rect.ymax());
        }
        else  {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(x.rect.xmin(),x.p.y(), x.rect.xmax() , x.p.y());
        }
        draw(x.lb);
        draw(x.rt);
        
    }
    // all points that are inside the rectangle. Inspired in the inorder traversal presented in the class videos. 
    // One of the tutors replied to a draw question in the forum mentioning a "pre-order" traversal, which in 
    // the end is what is being done in this range search (checked on wikipedia)
    
    public Iterable<Point2D> range(RectHV rect)  {    
        LinkedQueue<Point2D> pointsInTheRectangle = new LinkedQueue<Point2D>();
        range(root, pointsInTheRectangle, rect);         
        return pointsInTheRectangle;        
    }
    
    private void range(Node x, LinkedQueue<Point2D> pointsInTheRectangle, RectHV rect) {
        if (x == null) return;
        if (rect.contains(x.p)) pointsInTheRectangle.enqueue(x.p);
        if (rect.intersects(x.rect)) {  //traverses the tree only if the rectangle of the method intersects with the rectangle where the point lies
            range(x.lb, pointsInTheRectangle, rect);
            range(x.rt, pointsInTheRectangle, rect);            
        }        
    }    
    
    public Point2D nearest(Point2D p)    {    // a nearest neighbor in the set to point p; null if the set is empty 
        if (isEmpty()) {
            return null;
        }
        Point2D pointNearest = root.p;        
        double distance = p.distanceTo(pointNearest);
        return nearest(root,p ,pointNearest, distance );
    }    
    
    private Point2D nearest(Node x, Point2D p, Point2D pointNearest, double distance)    {
        if (x == null) return pointNearest;
        if (p.distanceTo(x.p) < p.distanceTo(pointNearest)) {
            //distance = p.distanceTo(x.p);
            pointNearest = x.p;
        }
        //explore the Node only if the distance of the point to the rectangle is closer to the distance found so far
        // there might be a bug in here, since I was thinking only for points outside of the rectangle
        if (x.rect.distanceTo(p) < p.distanceTo(pointNearest)) { 
            if(x.lb == null && x.rt == null) {
               return pointNearest;
            }
            else if(x.lb != null && x.rt == null) {
               pointNearest = nearest(x.lb, p, pointNearest, distance); 
            }
            else if(x.lb == null && x.rt != null) {
               pointNearest = nearest(x.rt, p, pointNearest, distance); 
            }            
            else if (x.lb.rect.contains(p)) {
               pointNearest = nearest(x.lb, p, pointNearest, distance);
               pointNearest = nearest(x.rt, p, pointNearest, distance);              
            }
            else if (x.rt.rect.contains(p)) {
               pointNearest = nearest(x.rt, p, pointNearest, distance);
               pointNearest = nearest(x.lb, p, pointNearest, distance);              
            }  
            // very important to have this part. Otherwise, the program will not look
            // in rectangles that do not contain the point. See example in the main method
            else  { 
               pointNearest = nearest(x.rt, p, pointNearest, distance);
               pointNearest = nearest(x.lb, p, pointNearest, distance);              
            }             
        }        
        return pointNearest;
    }
    
    //to debug
    private Iterable<Point2D> traverse()  {    
        LinkedQueue<Point2D> points= new LinkedQueue<Point2D>();
        traverse(root, points);         
        return points;        
    }
    
    //pre-order traverse just for debugging
    private void traverse(Node x, LinkedQueue<Point2D> q) {
        if (x == null) return;
        q.enqueue(x.p);
        traverse(x.lb, q);
        traverse(x.rt,q);        
    }
    
     
    public static void main(String[] args) throws FileNotFoundException       {
        KdTree kd = new KdTree();
        PointSET brute = new PointSET();
        /*Point2D[] pArray = new Point2D[4];
        pArray[0] = new Point2D(5, 10);
        pArray[1] = new Point2D(5.1, 6);
        pArray[2] = new Point2D(1, 2);
        pArray[3] = new Point2D(2, 3);
        
       kd.insert(pArray[0]);
       kd.insert(pArray[1]);
       kd.insert(pArray[2]);
       kd.insert(pArray[3]); 
                */
 
       /////////////////////////////////////////////////////////////////////
        /*
        Scanner in;
        in = new Scanner(new FileReader("circle10.txt"));
         while (in.hasNext()) {
            double x = in.nextDouble();
            double y = in.nextDouble();
            Point2D p = new Point2D(x, y);
            kd.insert(p);
            brute.insert(p);
        }
        
        for(Point2D p2D : kd.traverse()) {
            System.out.println(p2D);
        }
        */

        
        Point2D[] pArray = new Point2D[5];
        pArray[0] = new Point2D(0.5, 1);
        pArray[1] = new Point2D(0.46, 0.5);
        pArray[2] = new Point2D(0.9, 0.2);
        pArray[3] = new Point2D(0.51, 0.5); //closest point
        pArray[4] = new Point2D(0.95, 0.1);
        
       kd.insert(pArray[0]);
       kd.insert(pArray[1]);
       kd.insert(pArray[2]);
       kd.insert(pArray[3]);
       kd.insert(pArray[4]); 
        
       StdDraw.enableDoubleBuffering();
       StdDraw.setXscale(0, 1);
       StdDraw.setYscale(0, 1); 
        
        kd.draw();
        StdDraw.show();  
        
        kd.nearest(new Point2D(0.49, 0.49));
                
        System.out.println(kd.nearest(new Point2D(0.49 , 0.5))) ;
        System.out.println(brute.nearest(new Point2D(0.5, 1)));
        System.out.println("size is " + kd.size());
               
       
    }
        
}
