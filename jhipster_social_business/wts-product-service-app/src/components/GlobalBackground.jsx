import React, { useEffect, useRef } from 'react';
import Lottie from 'lottie-react';
import backgroundLake from '../assets/animations/background_lake.json';

const GlobalBackground = () => {
    const lottieRef = useRef(null);

    useEffect(() => {
        // Explicitly play on mount to ensure animation runs
        if (lottieRef.current) {
            lottieRef.current.play();
        }
    }, []);

    return (
        <div className="fixed inset-0 -z-10 w-full h-full overflow-hidden">
            <Lottie
                lottieRef={lottieRef}
                animationData={backgroundLake}
                loop={true}
                autoplay={true}
                className="w-full h-full object-cover"
            />
        </div>
    );
};

export default GlobalBackground;
