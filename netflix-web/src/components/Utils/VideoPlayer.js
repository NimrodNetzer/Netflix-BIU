// VideoPlayer.jsx
import React, { useRef, useState, useCallback, useEffect } from "react";
import {
  FaPlay,
  FaPause,
  FaUndo,
  FaRedo,
  FaAngleLeft,
  FaExpand,
  FaCompress,
  FaVolumeUp,
  FaVolumeMute,
} from "react-icons/fa";
import "./VideoPlayer.css";
import PropTypes from 'prop-types'; // Import PropTypes for type checking

const VideoPlayer = ({
  videoUrl,
  videoName,
  startFullscreen,
  play, // New prop to control autoplay
  onClose, // Destructure onClose
}) => {
  const videoRef = useRef(null);
  const containerRef = useRef(null);
  const [showControls, setShowControls] = useState(true);

  // Timer ref for hiding controls
  const timerRef = useRef(null);

  // Reset controls visibility timer
  const resetTimer = useCallback(() => {
    setShowControls(true);

    if (timerRef.current) {
      clearTimeout(timerRef.current);
    }

    timerRef.current = setTimeout(() => {
      setShowControls(false);
    }, 3000);
  }, []);

  useEffect(() => {
    const handleMouseMove = () => {
      resetTimer();
    };

    const container = containerRef.current;
    if (container) {
      container.addEventListener("mousemove", handleMouseMove);
      container.addEventListener("touchstart", handleMouseMove);
    }

    return () => {
      if (container) {
        container.removeEventListener("mousemove", handleMouseMove);
        container.removeEventListener("touchstart", handleMouseMove);
      }
      if (timerRef.current) {
        clearTimeout(timerRef.current);
      }
    };
  }, [resetTimer]);

  useEffect(() => {
    resetTimer();
    // eslint-disable-next-line
  }, []);

  // Playback states
  const [isPlaying, setIsPlaying] = useState(play); // Initialize based on 'play' prop
  const [progress, setProgress] = useState(0); // percentage
  const [duration, setDuration] = useState(0);
  const [currentTime, setCurrentTime] = useState(0);

  // Volume
  const [volume, setVolume] = useState(1);
  const [muted, setMuted] = useState(false);

  // Show/Hide volume slider on hover
  const [showVolumeSlider, setShowVolumeSlider] = useState(false);

  // Fullscreen
  const [isFullscreen, setIsFullscreen] = useState(false);

  // Play/Pause toggle
  const handlePlayPause = () => {
    if (!videoRef.current) return;
    if (isPlaying) {
      videoRef.current.pause();
    } else {
      videoRef.current.play();
    }
    setIsPlaying(!isPlaying);
  };

  // Skip forward/backward 10s
  const handleSkip = (seconds) => {
    if (!videoRef.current) return;
    videoRef.current.currentTime += seconds;
  };

  // When metadata is loaded, get duration
  const handleLoadedMetadata = () => {
    if (videoRef.current) {
      setDuration(videoRef.current.duration);
    }
  };

  // On time update, calculate current progress
  const handleTimeUpdate = () => {
    if (!videoRef.current) return;
    const current = videoRef.current.currentTime;
    const dur = videoRef.current.duration;
    const newProgress = (current / dur) * 100;
    setCurrentTime(current);
    setProgress(newProgress);

    // Update the CSS variable to dynamically change color
    const progressBar = document.querySelector(".progress");
    if (progressBar) {
      progressBar.style.setProperty("--progress", `${newProgress}%`);
    }
  };

  // Seek
  const handleSeek = (e) => {
    if (!videoRef.current) return;
    const manualChange = Number(e.target.value);
    videoRef.current.currentTime = (manualChange / 100) * duration;
    setProgress(manualChange);

    // Set the dynamic CSS variable to update the red progress
    e.target.style.setProperty("--progress", `${manualChange}%`);
  };

  // Volume slider change
  const handleVolumeChange = (e) => {
    if (!videoRef.current) return;
    const newVolume = Number(e.target.value);
    setVolume(newVolume);
    videoRef.current.volume = newVolume;
    if (newVolume === 0) {
      setMuted(true);
      videoRef.current.muted = true;
    } else {
      setMuted(false);
      videoRef.current.muted = false;
    }
  };

  // Mute/Unmute toggle
  const toggleMute = () => {
    if (!videoRef.current) return;
    setMuted(!muted);
    videoRef.current.muted = !muted;
  };

  // Fullscreen toggle
  const handleFullScreen = () => {
    const playerContainer = containerRef.current;
    if (!isFullscreen) {
      // Enter fullscreen
      if (playerContainer.requestFullscreen) {
        playerContainer.requestFullscreen();
      } else if (playerContainer.msRequestFullscreen) {
        playerContainer.msRequestFullscreen();
      } else if (playerContainer.mozRequestFullScreen) {
        playerContainer.mozRequestFullScreen();
      } else if (playerContainer.webkitRequestFullscreen) {
        playerContainer.webkitRequestFullscreen();
      }
    } else {
      // Exit fullscreen
      if (document.exitFullscreen) {
        document.exitFullscreen();
      } else if (document.msExitFullscreen) {
        document.msExitFullscreen();
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
      } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
      }
    }
  };

  // Listen to fullscreen change events to sync state
  useEffect(() => {
    const handleFullscreenChange = () => {
      const fullscreenElement =
        document.fullscreenElement ||
        document.mozFullScreenElement ||
        document.webkitFullscreenElement ||
        document.msFullscreenElement;

      setIsFullscreen(!!fullscreenElement);

      // If exiting fullscreen, trigger onClose
      if (!fullscreenElement) {
        onClose();
      }
    };

    document.addEventListener("fullscreenchange", handleFullscreenChange);
    document.addEventListener("mozfullscreenchange", handleFullscreenChange);
    document.addEventListener("webkitfullscreenchange", handleFullscreenChange);
    document.addEventListener("msfullscreenchange", handleFullscreenChange);

    return () => {
      document.removeEventListener("fullscreenchange", handleFullscreenChange);
      document.removeEventListener("mozfullscreenchange", handleFullscreenChange);
      document.removeEventListener("webkitfullscreenchange", handleFullscreenChange);
      document.removeEventListener("msfullscreenchange", handleFullscreenChange);
    };
  }, [onClose]); // Add onClose to dependencies

  // Format time (e.g. 1:23)
  const formatTime = (timeSec) => {
    if (!timeSec) return "0:00";
    const minutes = Math.floor(timeSec / 60);
    const seconds = Math.floor(timeSec - minutes * 60);
    return `${minutes}:${seconds < 10 ? "0" + seconds : seconds}`;
  };

  // Effect to start fullscreen if prop is true
  useEffect(() => {
    if (startFullscreen) {
      handleFullScreen();
    }
    // eslint-disable-next-line
  }, [startFullscreen]);

  // Effect to handle the 'play' prop for autoplay
  useEffect(() => {
    if (play && videoRef.current) {
      const playPromise = videoRef.current.play();
      if (playPromise !== undefined) {
        playPromise
          .then(() => {
            setIsPlaying(true);
            console.log("✅ Video playback started");
          })
          .catch((error) => {
            setIsPlaying(false);
            console.warn("❌ Playback failed:", error);
          });
      }
    } else if (!play && videoRef.current) {
      videoRef.current.pause();
      setIsPlaying(false);
    }
  }, [play]);
  

  // Function to exit fullscreen and close the VideoPlayer
  const handleExitFullscreen = () => {
    if (document.fullscreenElement) {
      document.exitFullscreen();
    } else if (document.webkitFullscreenElement) { /* Safari */
      document.webkitExitFullscreen();
    } else if (document.msFullscreenElement) { /* IE11 */
      document.msExitFullscreen();
    }
    // onClose will be called via fullscreenchange event listener
  };

  return (
    <div
      ref={containerRef}
      className={`player-container ${showControls ? "show" : "hide"} ${
        isFullscreen ? "fullscreen" : ""
      }`}
    >
      {/* Video Element */}
      <video
        ref={videoRef}
        className="video"
        src={videoUrl}
        onLoadedMetadata={handleLoadedMetadata}
        onTimeUpdate={handleTimeUpdate}
        onClick={handlePlayPause} // Click video to toggle play/pause
        muted={muted}
        volume={volume}
      />

      {/* Top Bar */}
      <div className="top-bar">
        <div className="left-actions">
          {/* Back Button */}
          {isFullscreen && (
            <button className="icon-btn back-btn" onClick={onClose}> {/* Changed onClick */}
              <FaAngleLeft />
            </button>
          )}
        </div>

        {/* Title / Episode Info */}
        <div className="episode-info">
          <p>{videoName}</p>
        </div>
      </div>

      {/* Center Controls (large icons) */}
      <div className="center-controls">
        <button onClick={() => handleSkip(-10)} className="skip-btn">
          10 <FaUndo />
        </button>
        <button className="play-btn" onClick={handlePlayPause}>
          {isPlaying ? <FaPause /> : <FaPlay />}
        </button>
        <button onClick={() => handleSkip(10)} className="skip-btn">
          10 <FaRedo />
        </button>
      </div>

      {/* Bottom Bar */}
      <div className="bottom-bar">
        {/* Progress Bar (Placed above the buttons) */}
        <div className="progress-container">
          <input
            type="range"
            className="progress"
            min={0}
            max={100}
            step={0.1}
            value={progress}
            onChange={handleSeek}
          />
        </div>

        {/* Time Display & Buttons */}
        <div className="bottom-controls">
          {/* Bottom-left: Volume (hover to reveal slider) */}
          <div className="bottom-left">
            <div
              className="volume-container"
              onMouseEnter={() => setShowVolumeSlider(true)}
              onMouseLeave={() => setShowVolumeSlider(false)}
            >
              <button className="icon-btn" onClick={toggleMute}>
                {muted || volume === 0 ? <FaVolumeMute /> : <FaVolumeUp />}
              </button>
              {showVolumeSlider && (
                <div className="volume-slider">
                  <input
                    type="range"
                    min={0}
                    max={1}
                    step={0.01}
                    value={muted ? 0 : volume}
                    onChange={handleVolumeChange}
                    orient="horizontal"
                  />
                </div>
              )}
            </div>
          </div>

          {/* Time Display */}
          <div className="time-display">
            {formatTime(currentTime)} / {formatTime(duration)}
          </div>

          {/* Bottom-right: Fullscreen */}
          <div className="bottom-right">
            <button className="icon-btn" onClick={handleFullScreen}>
              {isFullscreen ? <FaCompress /> : <FaExpand />}
            </button>
          </div>
        </div>
      </div>

      {/* Close Button (Optional) */}
      {/* Uncomment if you want an additional close button */}
      {/* 
      <button 
        onClick={handleExitFullscreen} 
        className="close-btn"
      >
        Close Video
      </button> 
      */}
    </div>
  );
};

// Define PropTypes for type checking
VideoPlayer.propTypes = {
  videoUrl: PropTypes.string.isRequired,
  videoName: PropTypes.string.isRequired,
  startFullscreen: PropTypes.bool,
  play: PropTypes.bool, // Add play to PropTypes
  onClose: PropTypes.func.isRequired, // Ensure onClose is required
};

// Export the component
export default VideoPlayer;
