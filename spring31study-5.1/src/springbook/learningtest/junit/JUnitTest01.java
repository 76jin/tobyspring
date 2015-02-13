package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * 2.5 학습 테스트로 배우는 스프링.
 *  - 학습 테스트 : 자신이 만들지 않은 프레임워크나 라이브러리 등에 테스트를 작성하는 것.
 *  - 목적 : 사용 방법을 익히려는 것. 얼마나 제대로 이해하고 있는지 확인하려고.
 *  - 장점
 *    1. 다양한 조건에 따른 기능을 손쉽게 확인해 볼 수 있다.
 *    2. 학습 테스트 코드를 개발 중에 참고할 수 있다.
 *    3. 프레임워크나 제품을 업그레이드할 때 호환성 검증을 도와준다.
 *    4. 테스트 작성에 대한 좋은 훈련이 된다. *****
 *    5. 새로운 기술을 공부하는 과정이 즐거워진다.
 * 
 *  - JUnit 테스트 오브젝트 생성에 대한 학습 테스트
 *    - not(결과) : (결과)를 부정하는 메처.
 *    - sameInstance(object) : 실제로 같은 오브젝트인지를 비교한다.
 *    - hasItem() : 컬렉션의 원소인지 비교.
 *    
 * @author kjlee
 *
 */
public class JUnitTest01 {

	static Set<JUnitTest01> testObjects = new HashSet<JUnitTest01>();
	
	@Test
	public void test1() {
		System.out.println("111" + this);
		System.out.println("111" + testObjects);
		assertThat(testObjects, is(not(hasItem(this))));
		testObjects.add(this);
	}
	
	@Test
	public void test2() {
		System.out.println("222" + this);
		System.out.println("222" + testObjects);
		assertThat(testObjects, is(not(hasItem(this))));
		testObjects.add(this);
	}
	
	@Test
	public void test3() {
		System.out.println("333" + this);
		System.out.println("333" + testObjects);
		assertThat(testObjects, is(not(hasItem(this))));
		testObjects.add(this);
	}
}
