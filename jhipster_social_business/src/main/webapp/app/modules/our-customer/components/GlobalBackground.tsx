import React, { useEffect, useRef } from 'react';
import Lottie, { LottieRefCurrentProps } from 'lottie-react';
import backgroundLake from 'app/assets/animations/background_lake.json';

const GlobalBackground = () => {
  const lottieRef = useRef<LottieRefCurrentProps>(null);

  useEffect(() => {
    // Explicitly play on mount to ensure animation runs
    if (lottieRef.current) {
      lottieRef.current.play();
    }
  }, []);

  return (
    <div className="position-fixed top-0 start-0 w-100 h-100 overflow-hidden" style={{ zIndex: -10 }}>
      <Lottie
        lottieRef={lottieRef}
        animationData={backgroundLake}
        loop={true}
        autoplay={true}
        className="w-100 h-100"
        style={{ objectFit: 'cover' }}
      />
    </div>
  );
};

export default GlobalBackground;
