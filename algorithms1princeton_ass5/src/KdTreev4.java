
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
 * Wrong results for nearest. Exercise: find the bug in the method nearest. Why doesn't it work?
 */
public class KdTreev4 {
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
    
    public KdTreev4() {     
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
        if(x.p.compareTo(p) ==0) return x;
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
    }
    
    public Iterable<Point2D> range(RectHV rect)  {    // all points that are inside the rectangle. Inspired in the inorder traversal 
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
        if (p.distanceTo(x.p) < distance) {
            distance = p.distanceTo(x.p);
            pointNearest = x.p;
        }
        if (x.rect.distanceTo(p) <= distance) { //explore the Node if the distance of the point to the rectangle is closer to the distance found so far
            
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
        }        
        return pointNearest;
    }
    
     
    public static void main(String[] args)       {
       KdTreev4 kd = new KdTreev4();
       Point2D[] pArray = new Point2D[4];
       pArray[0] = new Point2D(0.4767894789564789, 0.4767894789564789);
       pArray[1] = new Point2D(0.4767894789564789, 0.4767894789564789);
       pArray[2] = new Point2D(1, 2);
       pArray[3] = new Point2D(1, 2);
        
       kd.insert(pArray[0]);
       kd.insert(pArray[1]);
       kd.insert(pArray[2]);
       kd.insert(pArray[3]); 
       
     /*  RectHV rectangle = new RectHV(1,1,5,8);
       for (Point2D p: kd.range(rectangle)) {
            System.out.println(p);
        }
             */
       
        System.out.println(kd.nearest(new Point2D(4,6))) ;
        System.out.println("size is " + kd.size());
       
    }
        
}
