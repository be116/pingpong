class MathVec2 {
  
  MathVec2() {}
  
  float v2Dot ( PVector v1, PVector v2 ) {
    return v1.x * v2.x + v1.y * v2.y;
  }
  
  float v2Cross ( PVector v1, PVector v2 ) {
    return v1.x * v2.y - v1.y * v2.x;
  }
  
  int v2Side ( PVector v1, PVector v2 ) {
    float buf = v2Cross( v1, v2 );
    if ( buf > 0 ) return 1; // 右
    else if ( buf < 0 ) return -1; // 左
    else return 0; //線上
  }
  
  //s1->v1
  //s2->v2
  boolean isCrossed ( PVector s1, PVector s2, PVector v1, PVector v2, PVector cross ) {
    PVector v = new PVector( s2.x, s2.y );
    v.sub( s1 );
    
    float crs_v1_v2 = v2Cross( v1, v2 );
    if ( crs_v1_v2 == 0.0f ) {
      return false; 
    }
    
    float crs_v_v1 = v2Cross( v, v1 );
    float crs_v_v2 = v2Cross( v, v2 );
    
    float t1 = crs_v_v2 / crs_v1_v2;
    float t2 = crs_v_v1 / crs_v1_v2;
    
    if ( 0 <= t1 && t1 <= 1 && 0 <= t2 && t2 <= 1 ){}
    else  return false;
    
    cross.set( v1 );
    cross.mult( t1 );
    cross.add( s1 );
    
    return true;
  } 
}
