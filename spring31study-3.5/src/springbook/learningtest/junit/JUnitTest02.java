package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.either;


import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
 *  - 스프링 테스트 컨텍스트 테스트
 *    - either().or() : 둘 중 하나만 true이면 성공.
 *  
 *  - 버그 테스트
 *    - 코드에 오류가 있을 때 그 오류를 가장 잘 드러내줄 수 있는 테스트.
 *    - 일단 실패하도록 만들어야 한다.
 *    - 그다음에 성공할 수 있도록 애플리케이션 코드를 수정한다.
 *    - 그 후에 테스트가 성공하면 해결된 것이다.
 *    - 필요성과 장점
 *      1. 테스트의 완성도를 높여준다.
 *      2. 버그의 내용을 명확하게 분석하게 해준다.
 *      3. 기술적인 문제를 해결하는 데 도움이 된다.
 *        - 동일한 문제가 발생하는 가장 단순한 코드와 그에 대한 버그 테스트를 만들면 도움이 된다.
 *    - 동등 분할:
 *      - 같은 결과를 내는 값의 범위를 구분해서 각 대표 값으로 테스트한다.
 *      - 어떤 작업의 결과의 종류가 true, false, 예외라면, 세가지에 대한 테스트를 만듬.
 *    - 경계값 분석:
 *      - 에러는 동등 분할 범위의 경계에서 주로 발생하는 특징을 이용하여 근처값을 테스트.
 *      - 보통 숫자 입력인 경우: 음수, 0, 그 주변 값 또는 정수의 최대값, 최소값 등으로 테스트.
 *      
 * @author kjlee
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("junit.xml")
public class JUnitTest02 {
	
	@Autowired
	ApplicationContext context;
	
	static ApplicationContext contextObect = null;

	static Set<JUnitTest02> testObjects = new HashSet<JUnitTest02>();
	
	@Test
	public void test1() {
		assertThat(testObjects, is(not(hasItem(this))));
		testObjects.add(this);
		
		assertThat(contextObect == null || contextObect == this.context, is(true));
		contextObect = this.context;
	}
	
	@Test
	public void test2() {
		assertThat(testObjects, is(not(hasItem(this))));
		testObjects.add(this);
		
		assertTrue(contextObect == null || contextObect == this.context);
		contextObect = this.context;
	}
	
	@Test
	public void test3() {
		assertThat(testObjects, is(not(hasItem(this))));
		testObjects.add(this);
		
		assertThat(contextObect,
				either(is(nullValue())).or(is(this.context)));
		contextObect = this.context;
	}
}
