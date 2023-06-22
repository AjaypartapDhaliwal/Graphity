import React, { useEffect, useState } from 'react';
import '../CSS/LoadingScreen.css';

const LoadingScreen = () => {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false);
    }, 3000); // Change the duration as per your requirement

    return () => {
      clearTimeout(timer);
    };
  }, []);

  return (
    <div className={`loading-screen ${isVisible ? 'visible' : 'hidden'}`}>
      <div className="loader" />
      <h2>Loading...</h2>
    </div>
  );
};

export default LoadingScreen;
