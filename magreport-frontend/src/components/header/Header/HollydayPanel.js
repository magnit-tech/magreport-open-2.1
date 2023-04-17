import React from 'react';
import isHollyday from  '../../../HollydayFunctions';
import { HeaderCSS } from './HeaderCSS';

export default function HollydayPanel(props){
    const classes = HeaderCSS();

    const value = isHollyday();
    switch (value){
        case 0:
        case 1:
        case 2:
        return (
    <div className="b-page_newyear">
      <div className="b-page__content">
      <i className="b-head-decor">
      <i className="b-head-decor__inner b-head-decor__inner_n1">
      <div className="b-ball b-ball_n1 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n2 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n3 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n4 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n5 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n6 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n7 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_n8 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n9 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i1"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i2"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i3"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i4"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i5"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i6"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      </i>
       
      <i className="b-head-decor__inner b-head-decor__inner_n2">
      <div className="b-ball b-ball_n1 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n2 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n3 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n4 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n5 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n6 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n7 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n8 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_n9 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i1"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i2"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i3"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i4"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i5"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i6"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      </i>
      <i className="b-head-decor__inner b-head-decor__inner_n3">
       
      <div className="b-ball b-ball_n1 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n2 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n3 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n4 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n5 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n6 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n7 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n8 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n9 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_i1"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i2"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i3"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i4"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i5"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i6"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      </i>
      <i className="b-head-decor__inner b-head-decor__inner_n4">
      <div className="b-ball b-ball_n1 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_n2 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n3 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n4 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n5 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n6 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n7 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n8 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n9 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i1"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_i2"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i3"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i4"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i5"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i6"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      </i>
      <i className="b-head-decor__inner b-head-decor__inner_n5">
      <div className="b-ball b-ball_n1 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n2 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_n3 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n4 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n5 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n6 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n7 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n8 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n9 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i1"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i2"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_i3"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i4"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i5"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i6"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      </i>
      <i className="b-head-decor__inner b-head-decor__inner_n6">
      <div className="b-ball b-ball_n1 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n2 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n3 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_n4 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n5 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n6 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n7 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n8 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n9 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i1"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i2"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i3"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_i4"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i5"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i6"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      </i>
      <i className="b-head-decor__inner b-head-decor__inner_n7">
      <div className="b-ball b-ball_n1 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n2 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n3 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n4 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_n5 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n6 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n7 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n8 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_n9 b-ball_bounce"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i1"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i2"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i3"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i4"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
       
      <div className="b-ball b-ball_i5"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      <div className="b-ball b-ball_i6"><div className="b-ball__right"></div><div className="b-ball__i"></div></div>
      </i>
      </i>
       
    </div>
    </div>
    )
      /*  case 3: return (
        <div className={classes.roses}></div>
            )*/
        default: return (<div/>)
    }
};