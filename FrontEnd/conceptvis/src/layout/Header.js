import React, { useState, useEffect } from 'react';
import { VscGraph } from "react-icons/vsc";
import "../CSS/Header.css";

export default function Header({ isLoggedIn }) {

    const headStyle = isLoggedIn ?  'logged' : 'default';

    return (
        <div className={headStyle}>
            <h1>
                <span>G</span>
                <span>r</span>
                <span>a</span>
                <span>p</span>
                <span>h</span>
                <span>i</span>
                <span>t</span>
                <span>y</span>
                <span className='header-icon-span'><VscGraph className='header-icon'/></span>
            </h1>
        </div>
    );
}