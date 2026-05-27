package edu.cmu.cs.dennisc.media.animation;

import edu.cmu.cs.dennisc.animation.Animation;
import edu.cmu.cs.dennisc.animation.AnimationObserver;
import edu.cmu.cs.dennisc.media.MediaPlayerObserver;
import edu.cmu.cs.dennisc.media.Player;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class MediaPlayerAnimationTest {
  @After
  public void clearObserver() {
    MediaPlayerAnimation.EPIC_HACK_setAnimationObserver(null);
  }

  @Test
  public void updateStartsPlayerAndPausesThenResumesWhenTimeStopsChanging() {
    StubPlayer player = new StubPlayer();
    player.timeRemaining = 4.0;
    MediaPlayerAnimation animation = new MediaPlayerAnimation(player);

    assertEquals(4.0, animation.update(1.0, null), 0.0);
    assertEquals(1, player.startCalls);
    assertEquals(0, player.pauseCalls);
    assertEquals(0, player.resumeCalls);

    animation.update(1.0, null);
    assertEquals(0, player.pauseCalls);

    animation.update(1.0, null);
    assertEquals(1, player.pauseCalls);

    animation.update(2.0, null);
    assertEquals(1, player.resumeCalls);
  }

  @Test
  public void resetAllowsAnimationToStartPlaybackAgain() {
    StubPlayer player = new StubPlayer();
    MediaPlayerAnimation animation = new MediaPlayerAnimation(player);

    animation.update(0.5, null);
    animation.reset();
    animation.update(1.0, null);

    assertEquals(2, player.startCalls);
  }

  @Test
  public void observerPathReportsStartAndCompleteStopsPlayer() {
    StubPlayer player = new StubPlayer();
    MediaPlayerAnimation animation = new MediaPlayerAnimation(player);
    RecordingMediaPlayerObserver mediaObserver = new RecordingMediaPlayerObserver();
    RecordingAnimationObserver animationObserver = new RecordingAnimationObserver();
    MediaPlayerAnimation.EPIC_HACK_setAnimationObserver(mediaObserver);

    assertEquals(0.0, animation.update(3.0, null), 0.0);
    animation.complete(animationObserver);

    assertSame(animation, mediaObserver.animation);
    assertEquals(3.0, mediaObserver.startTime, 0.0);
    assertEquals(1, player.stopCalls);
    assertSame(animation, animationObserver.finishedAnimation);
    assertSame(player, animation.getPlayer());
  }

  private static final class StubPlayer extends Player {
    private int startCalls;
    private int stopCalls;
    private int pauseCalls;
    private int resumeCalls;
    private double timeRemaining;

    @Override
    public void realize() {
    }

    @Override
    public double getDuration() {
      return 0;
    }

    @Override
    public double getTimeRemaining() {
      return timeRemaining;
    }

    @Override
    public void start() {
      startCalls++;
    }

    @Override
    public void playUntilStop() {
    }

    @Override
    public void stop() {
      stopCalls++;
    }

    @Override
    public void pause() {
      pauseCalls++;
    }

    @Override
    public void resume() {
      resumeCalls++;
    }
  }

  private static final class RecordingMediaPlayerObserver implements MediaPlayerObserver {
    private MediaPlayerAnimation animation;
    private double startTime;

    @Override
    public void mediaPlayerStarted(MediaPlayerAnimation playerAnimation, double playTime) {
      this.animation = playerAnimation;
      this.startTime = playTime;
    }
  }

  private static final class RecordingAnimationObserver implements AnimationObserver {
    private Animation finishedAnimation;

    @Override
    public void started(Animation animation) {
    }

    @Override
    public void finished(Animation animation) {
      this.finishedAnimation = animation;
    }
  }
}
