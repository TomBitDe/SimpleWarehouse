/**
 * Sample timer controlled session bean classes for tests.<br>
 * <p>
 * Always use AbstractTimerSession for the implementation. Example:<br>
 * <p>
 * \@Singleton<br>
 * public class TimerJpaSessionsBean1 extends AbstractTimerSession {<br>
 *     ...<br>
 * }<br>
 * <p>
 * Call TimerJpaSessionsBeanN (N=1,2, ...) for all JPA related test runs in the application server.<br>
 * Call TimerOtherSessionsBean for all other test runs in the application server.<br>
 * <p>
 * This is a sample for a timed bean injecting \@Resource TimeService.
 */
package com.home.simplewarehouse.timed;
