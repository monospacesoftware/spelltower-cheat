package com.monospace;

import java.io.*;
import java.util.*;

public class SpellCheat
{
  public static void main(String args[])
    throws Exception
  {
    new SpellCheat(args[0], args[1]);
  }
  
  private static enum Adjacent
  {
    TL(-1,-1),
    TM(-1,0),
    TR(-1,1),
    ML(0,-1),
    MR(0,1),
    BL(1,-1),
    BM(1,0),
    BR(1,1);
    
    final int _y;
    final int _x;
    
    private Adjacent(int y, int x)
    {
      _y = y;
      _x = x;
    }
    
    public int getX()
    {
      return _x;
    }
    
    public int getY()
    {
      return _y;
    }
  }
  
  public static final char EMPTY = '.';
  public static final char SPACE = ' ';

  public static enum LetterValue 
  {
    a('a', 1),
    b('b', 3),
    c('c', 3),
    d('d', 2),
    e('e', 1),
    f('f', 4),
    g('g', 2),
    h('h', 4),
    i('i', 1),
    j('j', 8, true), // BLUE
    k('k', 5),
    l('l', 1),
    m('m', 3),
    n('n', 1),
    o('o', 1),
    p('p', 3),
    q('q', 10, true), // BLUE
    r('r', 1),
    s('s', 1),
    t('t', 1),
    u('u', 1),
    v('v', 4),
    w('w', 4),
    x('x', 8, true), // BLUE
    y('y', 4),
    z('z', 10, true), // BLUE
    SPACE(SpellCheat.SPACE, 0),
    EMPTY(SpellCheat.EMPTY, 0);
      
    private char _c;
    private int _value;
    private boolean _rowClearing = false;
    
    private LetterValue(char c, int value)
    {
      this(c, value, false);
    }
    
    private LetterValue(char c, int value, boolean rowClearing)
    {
      _c = c;
      _value = value;
      _rowClearing = rowClearing;
    }
    
    public char getChar()
    {
      return _c;
    }

    public int getValue()
    {
      return _value;
    }
    
    public boolean isRowClearing()
    {
      return _rowClearing;
    }
  }
  
  public class Box
  {
    private int _x;
    private int _y;
    
    private LetterValue _letterValue;
    
    public Box(char c, int x, int y)
    {
      _x = x;
      _y = y;
      
      if (c == EMPTY)
        _letterValue = LetterValue.EMPTY;
      else if (c == SPACE)
        _letterValue = LetterValue.SPACE;
      else
        _letterValue = LetterValue.valueOf(Character.toString(Character.toLowerCase(c)));
    }
    
    public char getChar()
    {
      return _letterValue.getChar();
    }
    
    public int getX()
    {
      return _x;
    }
    
    public int getY()
    {
      return _y;
    }
    
    public boolean isSpace()
    {
      return _letterValue == LetterValue.SPACE;
    }
    
    public boolean isBlank()
    {
      return _letterValue == LetterValue.EMPTY;
    }

    public boolean hasValue()
    {
      return _letterValue.getValue() > 0;
    }
    
    public int getValue()
    {
      return _letterValue.getValue();
    }
    
    public boolean isRowClearing()
    {
      return _letterValue.isRowClearing();
    }
    
    public String toString()
    {
      //return String.format("%s [%s,%s] (%s)", getChar(), _x, _y, getValue());
      //return _letterValue.getChar() + "[" + _x + "," + _y + "]";
      return String.format("%s(%s)", getChar(), getValue());
    }
    
    public boolean equals(Box box)
    {
      return equals(box.getX(), box.getY());
    }
    
    public boolean equals(int x, int y)
    {
      return x == _x && y == _y;
    }
  }
  
  public class Tower implements Iterable<Box>
  {
    private Box[][] _matrix;
    
    private int _height;
    private int _width;

    public Tower(Box[][] matrix)
    {
      _matrix = matrix;
      
      _height = _matrix.length;
      _width = _matrix[0].length;
    }
    
    public int getWidth()
    {
      return _width;
    }
    
    public int getHeight()
    {
      return _height;
    }
    
    public Box getBox(int x, int y)
    {
      if (y < 0 || y >= _height || x < 0 || x >= _width)
        return null;
      
      return _matrix[y][x];
    }
    
    public Box getAdjacent(Box box, Adjacent adjacent)
    {
      int x = box.getX() + adjacent.getX();
      int y = box.getY() + adjacent.getY();
      
      return getBox(x, y);
    }
    
    public Iterable<Box> adjacents(final Box box)
    {
      return new Iterable<Box>()
      {
        public Iterator<Box> iterator()
        {
          return new Iterator<Box>() 
          {
            private int _i = 0;
            
            public void remove()
            {
              
            }
            
            public Box next()
            {
              return _tower.getAdjacent(box, Adjacent.values()[_i++]);
            }
            
            public boolean hasNext()
            {
              return _i < Adjacent.values().length;
            }
          };
        }
      };      
    }

    public Iterator<Box> iterator()
    {
      return new Iterator<SpellCheat.Box>()
      {
        private int _y = 0;
        private int _x = 0;
        
        public void remove()
        {
          
        }
        
        public Box next()
        {
          if (! hasNext())
            return null;
          
          Box box = _matrix[_y][_x];
          if (_x == _width-1) {
            _x = 0;
            _y++;
          } else {
            _x++;
          }
          return box;
        }
        
        public boolean hasNext()
        {
          if (_y >= _height)
            return false;
          return true;
        }
      };
    }
  }
  
  public class Trail implements Comparable<Trail>
  {
    private Box[] _boxes;
    
    private int _value = -1;
    
    public Trail()
    {
      _boxes = new Box[0];
    }

    public Trail(Box box)
    {
      _boxes = new Box[1];
      _boxes[0] = box;
      
      //_value = calcValue();
    }
    
    public Trail(Trail trail, Box box)
    {
      _boxes = new Box[trail.size()+1];
      System.arraycopy(trail.getBoxes(), 0, _boxes, 0, trail.size());
      _boxes[_boxes.length-1] = box;
      
      //_value = calcValue();
    }
    
    public int size()
    {
      return _boxes.length;
    }

    
    public Box[] getBoxes()
    {
      return _boxes;
    }
    
    public boolean contains(Box box)
    {
      for(Box b : _boxes)
        if (b.equals(box))
          return true;
      return false;
    }
    
    public boolean contains(int x, int y)
    {
      for(Box b : _boxes)
        if (b.equals(x, y))
          return true;
      return false;
    }
    
    public Box get(int x, int y)
    {
      for(Box b : _boxes)
        if (b.equals(x, y))
          return b;
      return null;
    }
    
    public String toWord()
    {
      StringBuilder word = new StringBuilder();
      
      for(Box box : _boxes)
        word.append(box.getChar());
      
      return word.toString();
    }
    
    public int getValue()
    {
      if (_value < 0)
        _value = calcValue();
      return _value;
    }
    
    private int calcValue()
    {
      String word = toWord();
      System.out.println();
      System.out.println("Calculating value of " + word);
      System.out.println(toString());
      
      int columns = _tower.getWidth();
      int len = this.size();
      int acc = 0;

      HashSet<Box> bonusBoxes = new HashSet<Box>();

      for(Box box : this.getBoxes()) {
        System.out.println(box);
        
        acc += box.getValue();
      
        if (len > 4) {
          Box north = _tower.getAdjacent(box, Adjacent.TM);
          if (north != null && north.hasValue()) {
            if (bonusBoxes.add(north)) 
              System.out.println(" +NORTH: " + north);
//            bonusBoxes.add(north);
          }
          
          Box east = _tower.getAdjacent(box, Adjacent.MR);
          if (east != null && east.hasValue()) {
            if (bonusBoxes.add(east))
              System.out.println(" +EAST: " + east);
//            bonusBoxes.add(east);
          }
          
          Box south = _tower.getAdjacent(box, Adjacent.BM);
          if (south != null && south.hasValue()) {
            if (bonusBoxes.add(south))
              System.out.println(" +SOUTH: " + south);
//            bonusBoxes.add(south);
          }
          
          Box west = _tower.getAdjacent(box, Adjacent.ML);
          if (west != null && west.hasValue()) {
            if (bonusBoxes.add(west))
              System.out.println(" +WEST: " + west);
//            bonusBoxes.add(west);
          }
        }
        
        if (box.isRowClearing()) {
          int row = box.getY();
          for(int column=0; column<columns; column++) {
            Box rowBox = _tower.getBox(row, column);
            if (!rowBox.equals(box)) {
              if (bonusBoxes.add(rowBox))
                System.out.println(" +ROW: " + rowBox);
//              bonusBoxes.add(rowBox);
            }
          }
        }
      }
      
      for(Box bonus : bonusBoxes) {
        acc += bonus.getValue();
      }
      
      int ret = acc * len;
      
      System.out.println(String.format("%s: %s points * %s length = %s", word, acc, len, ret));
      
      return ret;
    }
    
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      
      sb.append(toWord());
      sb.append("(");
      sb.append(size());
      sb.append(")\n");
      
      for(Box box : _boxes) {
        sb.append(" " + box);
        sb.append("\n");
      }
      
      return sb.toString();
    }
    
    public String toDisplay(int width, int height)
    {
      StringBuilder sb = new StringBuilder();
      sb.append(toWord());
      sb.append("(");
      sb.append(getValue());
      sb.append(")\n");
      sb.append("  ");
      for(int x=0; x<width; x++) {
        sb.append(x+1);
      }
      sb.append("\n");
      for (int y=0; y<height; y++) {
        if (y < 9)
          sb.append(" ");
        sb.append(y+1);
        for(int x=0; x<width; x++) {
          Box b = get(x, y);
          if (b != null)
            sb.append(b.getChar());
          else
            sb.append(".");
        }
        sb.append("\n");
      }
      
      return sb.toString();
    }

    @Override
    public int compareTo(Trail o)
    {
      if (o.getValue() > getValue())
        return -1;
      if (o.getValue() < getValue())
        return 1;
      return 0;
    }
  }
  
  private Node _dictionary;
  private Tower _tower;
  private List<Trail> _wordTrails = new ArrayList<Trail>();
  
  public SpellCheat(String dictionaryFile, String towerFile)
    throws Exception
  {
    _dictionary = loadDictionary(dictionaryFile);
    _tower = loadTower(towerFile);
    
    for(Box box : _tower) {
      //System.out.println("working on " + box);
      search(new Trail(), box, _dictionary);
    }
    
    System.out.println("Found " + _wordTrails.size() + " words");
    
    Collections.sort(_wordTrails);
    
    for(Trail trail : _wordTrails) {
      System.out.println(trail.toDisplay(_tower.getWidth(), _tower.getHeight()));
    }
  }
  
  private void search(Trail trail, Box box, Node parent)
  {
    if (box == null || box.isBlank() || trail.contains(box))
      return;
    
    char c = box.getChar();
    Node node = parent.get(c);
    
    if (node == null) {
      //System.out.println(trail.toString() + c + " is a dead end");
    } else {
      Trail newTrail = new Trail(trail, box);
      String word = newTrail.toWord();
      if (node.isWord() && word.length() > 2) {
        _wordTrails.add(newTrail);
        //System.out.println(word + " is a word: found " + _wordTrails.size() + " words");
      } else {
        //System.out.println(word + " is not a word");
      }
      
      if (node.hasChildren()) {
        //System.out.println(" " + word + " has children, continuing search");
        for(Box adjacent : _tower.adjacents(box)) {
          search(newTrail, adjacent, node);
        }
      } else {
        //System.out.println(" " + word + " does not have children, halting search");
      }
    }
  }
  
  private boolean isValid(int x, int y, int w, int h)
  {
    return x >= 0 && x < w && y >= 0 && y < h;
  }
  
  private void printNode(Node node, int depth)
  {
    if (node.isWord()) {
      print(node.getWord(), depth);
    } else {
      for(Node child : node.getChildren()) {
        print(Character.toString(child.getChar()), depth);
        printNode(child, ++depth);
      }
    }
  }
  
  private void print(String s, int depth)
  {
    for(int i=0; i<depth; i++)
      System.out.print(" ");
    System.out.println(s);
  }
  
  private Node loadDictionary(String file)
    throws Exception
  {
    Node root = new Node();
    
    BufferedReader br = new BufferedReader(new FileReader(file));

    String word = null;
    while(true) {
      word = br.readLine();
      if (word == null)
        break;
      
      Node node = root;
      
      for(char c : word.toCharArray()) {
        node = node.addChild(c);
      }
      
      node.setWord(word);
    }
    
    br.close();
    
    return root;
  }
  
  public class Node
  {
    private char _c;
    private Map<Character, Node> _children = 
      new HashMap<Character, Node>(26);
    private String _word;
    
    public Node() 
    {
      _c = 0;
    }
    
    public Node(char c) 
    {
      _c = c;
    }
    
    public Node addChild(char c)
    {
      Node child = _children.get(c);
      if (child != null) {
        return child;
      }
      child = new Node(c);
      _children.put(c, child);
      return child;
    }
    
    public void setWord(String word)
    {
      _word = word;
    }
    
    public boolean isRoot()
    {
      return _c == 0;
    }
    
    public char getChar()
    {
      return _c;
    }
    
    public String getWord()
    {
      return _word;
    }
    
    public Collection<Node> getChildren()
    {
      return _children.values();
    }
    
    public Node get(char c)
    {
      return _children.get(c);
    }

    public boolean isWord()
    {
      return _word != null;
    }
    
    public boolean matches(String word)
    {
      return word.equals(_word);
    }
    
    public boolean hasChildren()
    {
      return ! _children.isEmpty();
    }
    
    public String toString()
    {
      return (_c == 0 ? "ROOT" : _c) + "[" + _children.size() + "]" + (isWord() ? ":" + _word: "");
    }
  }
  
  public Tower loadTower(String file)
    throws Exception
  {
    BufferedReader br = new BufferedReader(new FileReader(file));
    
    List<String> lines = new ArrayList<String>();
    
    String s = null;
    while(true) {
      s = br.readLine();
      if (s == null)
        break;
      
      lines.add(s);
    }
    
    br.close();
    
    int width = -1;
    for(String line : lines) {
      if (width == -1)
        width = line.length();
      else if (width != line.length())
        throw new IllegalArgumentException("all lines must be the same length");
    }
    
    int height = lines.size();
    
    Box[][] matrix = new Box[height][width];
    
    for(int h=0; h<height; h++) {
      String line = lines.get(h);
      for(int w=0; w<width; w++) {
        matrix[h][w] = new Box(line.charAt(w), w, h);
      }
    }
    
    return new Tower(matrix);
  }
  
}
