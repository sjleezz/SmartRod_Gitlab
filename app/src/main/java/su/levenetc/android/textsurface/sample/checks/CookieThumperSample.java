package su.levenetc.android.textsurface.sample.checks;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import su.levenetc.android.textsurface.contants.Align;
import su.levenetc.android.textsurface.Text;
import su.levenetc.android.textsurface.TextBuilder;
import su.levenetc.android.textsurface.TextSurface;
import su.levenetc.android.textsurface.animations.Alpha;
import su.levenetc.android.textsurface.animations.ChangeColor;
import su.levenetc.android.textsurface.animations.Circle;
import su.levenetc.android.textsurface.animations.Delay;
import su.levenetc.android.textsurface.animations.Parallel;
import su.levenetc.android.textsurface.animations.Rotate3D;
import su.levenetc.android.textsurface.animations.Sequential;
import su.levenetc.android.textsurface.animations.ShapeReveal;
import su.levenetc.android.textsurface.animations.SideCut;
import su.levenetc.android.textsurface.animations.Slide;
import su.levenetc.android.textsurface.animations.TransSurface;
import su.levenetc.android.textsurface.contants.Direction;
import su.levenetc.android.textsurface.contants.Pivot;
import su.levenetc.android.textsurface.contants.Side;

/**
 * Created by Eugene Levenetc.
 */
public class CookieThumperSample {

	public static void play(TextSurface textSurface, AssetManager assetManager) {

		final Typeface robotoBlack = Typeface.createFromAsset(assetManager, "fonts/Typo_SsangmunDongB.ttf");
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTypeface(robotoBlack);

		Text textA = TextBuilder
				.create("건국")
				.setPaint(paint)
				.setSize(64)
				.setAlpha(0)
				.setColor(0x00B700)
				.setPosition(Align.SURFACE_CENTER).build();

		Text textB = TextBuilder
				.create("대학교")
				.setPaint(paint)
				.setSize(44)
				.setAlpha(0)
				.setColor(0x00B700)
				.setPosition(Align.BOTTOM_OF, textA).build();

		Text textC = TextBuilder
				.create(" 졸업 프로젝트")
				.setPaint(paint)
				.setSize(44)
				.setAlpha(0)
				.setColor(Color.RED)
				.setPosition(Align.RIGHT_OF, textB).build();

		Text textD = TextBuilder
				.create(" 애플리케이션")
				.setPaint(paint)
				.setSize(44)
				.setAlpha(0)
				.setColor(0xCC3D3D)
				.setPosition(Align.BOTTOM_OF, textC).build();

		Text textE = TextBuilder
				.create("for ")
				.setPaint(paint)
				.setSize(44)
				.setAlpha(0)
				.setColor(Color.BLACK)
				.setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textD).build();

		Text textF = TextBuilder
				.create("Android")
				.setPaint(paint)
				.setSize(44)
				.setAlpha(0)
				.setColor(0x00B700)
				.setPosition(Align.RIGHT_OF, textE).build();

		Text textG = TextBuilder
				.create("시각장애인을 위한")
				.setPaint(paint)
				.setSize(44)
				.setAlpha(0)
				.setColor(Color.BLACK)
				.setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textF).build();

		Text textH = TextBuilder
				.create("보행 안전")
				.setPaint(paint)
				.setSize(44)
				.setAlpha(0)
				.setColor(Color.RED)
				.setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textG).build();

		Text textI = TextBuilder
				.create("객체 탐지 앱")
				.setPaint(paint)
				.setSize(44)
				.setAlpha(0)
				.setColor(Color.BLACK)
				.setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textH).build();

		Text textJ = TextBuilder
				.create("Smart Rod")
				.setPaint(paint)
				.setSize(44)
				.setAlpha(0)
				.setColor(Color.BLACK)
				.setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textI).build();

		textSurface.play(
				new Sequential(
						ShapeReveal.create(textA, 750, SideCut.show(Side.LEFT), false),
						new Parallel(ShapeReveal.create(textA, 600, SideCut.hide(Side.LEFT), false), new Sequential(Delay.duration(300), ShapeReveal.create(textA, 600, SideCut.show(Side.LEFT), false))),
						new Parallel(new TransSurface(500, textB, Pivot.CENTER), ShapeReveal.create(textB, 1300, SideCut.show(Side.LEFT), false)),
						Delay.duration(300),
						new Parallel(new TransSurface(1000, textC, Pivot.CENTER), Slide.showFrom(Side.LEFT, textC, 750), ChangeColor.to(textC, 750, Color.BLACK)),
						Delay.duration(200),
						new Parallel(TransSurface.toCenter(textD, 500), Rotate3D.showFromSide(textD, 750, Pivot.TOP)),
						Delay.duration(200),
						new Parallel(TransSurface.toCenter(textE, 500), Slide.showFrom(Side.TOP, textE, 500)),
						Delay.duration(100),
						new Parallel(TransSurface.toCenter(textF, 750), Slide.showFrom(Side.LEFT, textF, 500)),
						Delay.duration(700),
						new Parallel(
								new TransSurface(1500, textI, Pivot.CENTER),
								new Sequential(
										new Sequential(ShapeReveal.create(textG, 500, Circle.show(Side.CENTER, Direction.OUT), true)),
										new Sequential(ShapeReveal.create(textH, 500, Circle.show(Side.CENTER, Direction.OUT), true)),
										new Sequential(ShapeReveal.create(textI, 500, Circle.show(Side.CENTER, Direction.OUT), true)),
										new Sequential(ShapeReveal.create(textJ, 500, Circle.show(Side.CENTER, Direction.OUT), true))
								)
						),
						Delay.duration(300),
						new Parallel(
								Alpha.hide(textA, 100), Alpha.hide(textB, 100), Alpha.hide(textC, 100),
								Alpha.hide(textD, 100), Alpha.hide(textE, 100), Alpha.hide(textF, 100),
								ShapeReveal.create(textG, 1500, SideCut.hide(Side.LEFT), false),
								new Sequential(Delay.duration(250), ShapeReveal.create(textH, 1500, SideCut.hide(Side.LEFT), false)),
								new Sequential(Delay.duration(500), ShapeReveal.create(textI, 1500, SideCut.hide(Side.LEFT), false)),
								new Sequential(Delay.duration(750), ShapeReveal.create(textJ, 1500, SideCut.hide(Side.LEFT),false), ChangeColor.to(textJ, 800, Color.GREEN))
						)
				)
		,Delay.duration(500));

	}

}
