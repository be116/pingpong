class Ball {
 
  PVector zeroPosition;
  float radius;
  color col;
  float force;

  PVector position;
  PVector velocity;
  
  boolean onGround;
  boolean isRacketting;
  boolean isFalling;
  boolean onLeftWall, onRightWall;
  
  Ball ( PVector p, float r, color c ) {
    zeroPosition = p;
    radius = r;
    col = c;
    
    position = new PVector( 0, 0 );
    velocity = new PVector( 0, 0 );

    init();
  }
  
  void init ( ) {
    position.set( zeroPosition );
    //velocity.limit( 20 );
    force = 20;
    onGround = false;
    isRacketting = false;
    isFalling = false;
    onLeftWall = onRightWall = false;
  }
  
  void jump ( float a ) {
    if ( !onGround ) return;
    velocity.y += a;
    init();
  }
  
  float updateByRadius () {
    float deltaTime = 1.0;
    if ( velocity.mag() != 0 ) {
      deltaTime = radius / velocity.mag();
      //deltaTime = ( - velocity.mag() + sqrt( pow( velocity.mag(), 2 ) + 2 * gravity * radius ) ) / gravity;
    }
    if ( ellapsedTime + deltaTime > 1.0 ) deltaTime = 1.0 - ellapsedTime;
    return updateByDeltaTime( deltaTime );
  }
  
  // 実際の更新時間を返す
  float updateByDeltaTime ( float deltaTime ) {
    //一回の更新が1Fを超えない
    if ( ellapsedTime + deltaTime > 1.0 ) deltaTime = 1.0 - ellapsedTime;
    
    if ( velocity.y > 0 ) isFalling = true;
    
    //位置更新
    PVector displacement = new PVector( 0, 0 );
    displacement.set( velocity );
    displacement.mult( deltaTime );
    position.add( displacement );
    
    //画面外の判定
    onGround = position.y - radius >= height;
    onLeftWall = position.x - radius < 0;
    onRightWall = position.x + radius > width;
    
    //衝突判定   
    PVector s1 = new PVector ( 0, 0 );
    PVector s2 = new PVector ( 0, 0 );
    PVector v1 = new PVector ( 0, 0 );
    PVector v2 = new PVector ( 0, 0 );
    
    s2.set( racket.point[0] );
    v2.set( racket.point[1] );
    v2.sub( racket.point[0] );
    PVector tagNormal = new PVector( 0, 0 );
    tagNormal.set( racket.point[1] ); tagNormal.sub( racket.point[0] ); tagNormal.normalize();
    tagNormal.rotate( HALF_PI ); //判定対象の法線ベクトルを取得
    s1.set( position );
    v1.set( tagNormal ); v1.mult( - radius );

    PVector cross = new PVector( 0, 0 );
    isRacketting = mathVec2.isCrossed ( s1, s2, v1, v2, cross );
    if ( !isFalling ) isRacketting = false; 
    if ( isRacketting ) onGround = false;
    
    //衝突応答
    if ( onGround ) {
      position.set( zeroPosition );
    }
    
    //めりこみの解消
    if ( isRacketting ) {
      //修正量の計算
      PVector modification = new PVector( 0, 0 );
      float dist = PVector.dist( cross, position );
      modification.set( tagNormal );
      modification.mult( radius - dist );
      
      position.add( modification );
    } else if ( onLeftWall ) {
      position.x = 0 + radius;
    } else if ( onRightWall ) {
      position.x = width - radius;
    }
    
    //速度の更新
    if ( onGround ) {
      velocity.set( 0, 0 );
    } else if ( isRacketting ) {
      float tagRad = atan2( v2.y, v2.x );
      float velocityRad = atan2( velocity.y, velocity.x );
      float nyusyaRad = tagRad - velocityRad;
      velocity.rotate( 2 * nyusyaRad );
      float dist = PVector.dist( cross, racket.position );
      velocity.setMag( force );
      //velocity.mult( racket.calculateCritical( dist ) );
      score.scorePlus();
    } else if ( onRightWall || onLeftWall ) {
      velocity.x *= -1;
    }else {
      //重力
      velocity.y += gravity * deltaTime;
    }
    
    return deltaTime;
  }
  
  void draw ( ) {
    //stroke( col );
    noStroke();
    fill( col );
    ellipseMode(RADIUS);
    ellipse( position.x, position.y, radius, radius );
  }
  
}
