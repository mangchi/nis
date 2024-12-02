package mil.gdl.nis.aspect;

import com.privacy.pCrypto;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import mil.gdl.nis.annotation.Log;
import mil.gdl.nis.annotation.NoAuth;
import mil.gdl.nis.annotation.Page;
import mil.gdl.nis.annotation.PageExcel;
import mil.gdl.nis.annotation.Trnc;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.error.ErrorCode;
import mil.gdl.nis.cmmn.util.StringUtil;
import mil.gdl.nis.cmmn.vo.PageInfo;
import mil.gdl.nis.dataset.vo.DatasetVo;
import mil.gdl.nis.exception.BizException;
import mil.gdl.nis.log.service.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Component
@Aspect
public class ControllerAspect {
	private static final Logger log = LoggerFactory.getLogger(mil.gdl.nis.aspect.ControllerAspect.class);

	public ControllerAspect(LogService logService, SessionData sessionData) {
		this.logService = logService;
		this.sessionData = sessionData;
	}

	private final LogService logService;

	private final SessionData sessionData;

	@Value("${spring.profiles.active:}")
	private String activeProfile;

	@Pointcut("execution(* mil.gdl.nis..*.*Controller.*(..))")
	public void commonPointcut() {
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before("commonPointcut()")
	public void callBeforeController(JoinPoint joinPoint) throws Throwable {
		Map<String, Object> param = null;

		try {
			String replaceStr = "&#36;";
			MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
			Page page = methodSignature.getMethod().<Page>getAnnotation(Page.class);
			PageExcel pageExcel = methodSignature.getMethod().<PageExcel>getAnnotation(PageExcel.class);
			Trnc trnc = methodSignature.getMethod().<Trnc>getAnnotation(Trnc.class);
			for (Object obj : joinPoint.getArgs()) {
				if (obj instanceof Map) {
					Map<String, Object> mapParam = (Map<String, Object>) obj;
					Set<?> keySet = mapParam.keySet();
					if (page != null) {
						mapParam.put("pageInfo",
								PageInfo.builder().pageNum(Integer.parseInt((String) mapParam.get("pageNum")))
										.rowPerPage(Integer.parseInt((String) mapParam.get("rowPerPage")))
										.orderBy(page.sort().equals("ASC") ? "DESC" : "ASC").build());
					}

					if (pageExcel != null) {
						mapParam.put("pageInfo",
								PageInfo.builder().pageNum(Integer.parseInt((String) mapParam.get("pageNum")))
										.rowPerPage(Integer.parseInt((String) mapParam.get("rowPerPage")))
										.orderBy(pageExcel.sort().equals("ASC") ? "DESC" : "ASC").build());
					}

					if (!mapParam.containsKey("schUnitId")) {
						if (this.sessionData.getUserVo() == null) {
							mapParam.put("schUnitId", "0");
						} else {

							mapParam.put("schUnitId", this.sessionData.getUserVo().getUnitId());
						}

					} else if (mapParam.get("schUnitId").equals("")) {
						mapParam.put("schUnitId", this.sessionData.getUserVo().getUnitId());
					}

					for (Iterator<?> keyIter = keySet.iterator(); keyIter.hasNext();) {
						String key = (String) keyIter.next();
						Object entryObj = mapParam.get(key);
						if (entryObj instanceof Map) {
							param = (Map<String, Object>) entryObj;
							Set<?> entrySet = param.keySet();
							for (Iterator<?> entryIter = entrySet.iterator(); entryIter.hasNext();) {
								String mapKey = (String) entryIter.next();
								if (!mapKey.equals("userPw") && String.valueOf(param.get(mapKey)).indexOf("$") >= 0) {
									param.put(mapKey, String.valueOf(param.get(mapKey)).replaceAll("[$]", replaceStr));
								}
							}
						}

						if (entryObj instanceof List) {
							List<Map<String, Object>> list = (List<Map<String, Object>>) entryObj;
							for (Map<String, Object> item : list) {
								Set<?> itemSet = item.keySet();
								for (Iterator<?> itemIter = itemSet.iterator(); itemIter.hasNext();) {
									String itemKey = (String) itemIter.next();
									if (!itemKey.equals("userPw")
											&& String.valueOf(item.get(itemKey)).indexOf("$") >= 0) {
										item.put(itemKey,
												String.valueOf(item.get(itemKey)).replaceAll("[$]", replaceStr));
									}
								}

							}

						}

					}

				} else if (trnc != null && !(obj instanceof mil.gdl.nis.user.vo.UserVo)
						&& !(obj instanceof java.security.Principal)) {
					Class<? extends Object> paramClass = (Class) obj.getClass();
					Field[] fields = paramClass.getDeclaredFields();
					for (Field f : fields) {
						f.setAccessible(true);
						if (!f.getName().equals("userPw")) {
							String fileVal = String.valueOf(getValueFromField(f, paramClass, obj));
							log.debug("file Name:{}", f.getName());
							if (fileVal.indexOf("$") >= 0) {
								f.set(obj, fileVal.replaceAll("[$]", replaceStr));
							}
						}
					}
				}
			}

			log.debug(methodSignature.getName() + " param:{}", joinPoint.getArgs());
		} catch (Exception e) {
			log.error("ControllerAspect error:", e);
		}
	}

	private Object getValueFromField(Field field, Class<?> clazz, Object obj) {
		for (Method method : clazz.getMethods()) {
			String methodName = method.getName();
			if (((methodName.startsWith("get") && methodName.length() == field.getName().length() + 3)
					|| (methodName.startsWith("is") && methodName.length() == field.getName().length() + 2))
					&& methodName.toLowerCase().endsWith(field.getName().toLowerCase())) {
				try {
					return method.invoke(obj, new Object[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@AfterReturning(pointcut = "commonPointcut()", returning = "returnValue")
	public void callAfterController(JoinPoint joinPoint, Object returnValue) throws BizException, Throwable {
		String replaceStr = "&#36;";
		try {
			MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
			Map<String, Object> returnParam = null;
			if (returnValue instanceof Map) {
				Trnc trnc = methodSignature.getMethod().<Trnc>getAnnotation(Trnc.class);
				Page page = methodSignature.getMethod().<Page>getAnnotation(Page.class);
				Map<String, Object> map = (Map<String, Object>) returnValue;

				Set<?> keySet = map.keySet();
				for (Iterator<?> keyIter = keySet.iterator(); keyIter.hasNext();) {
					String key = (String) keyIter.next();
					Object entryObj = map.get(key);
					if (entryObj instanceof Map) {
						returnParam = (Map<String, Object>) entryObj;
						Set<?> entrySet = returnParam.keySet();
						for (Iterator<?> entryIter = entrySet.iterator(); entryIter.hasNext();) {
							String mapKey = (String) entryIter.next();
							if (!mapKey.equals("userPw")
									&& String.valueOf(returnParam.get(mapKey)).indexOf(replaceStr) >= 0) {
								returnParam.put(mapKey, String.valueOf(returnParam.get(mapKey)).replaceAll(replaceStr,
										Matcher.quoteReplacement("$")));
							}

							if (this.activeProfile.equals("prod")) {
								if (mapKey.equals("collectionDt")) {
									returnParam.put(mapKey,
											pCrypto.Decrypt("normal", String.valueOf(returnParam.get(mapKey)), "", 0));
								}
								if (mapKey.equals("collectionPos")) {
									returnParam.put(mapKey,
											pCrypto.Decrypt("normal", String.valueOf(returnParam.get(mapKey)), "", 0));
								}
								if (mapKey.equals("dataCoordX")) {
									returnParam.put(mapKey,
											pCrypto.Decrypt("normal", String.valueOf(returnParam.get(mapKey)), "", 0));
								}
								if (mapKey.equals("dataCoordY")) {
									returnParam.put(mapKey,
											pCrypto.Decrypt("normal", String.valueOf(returnParam.get(mapKey)), "", 0));
								}
								if (mapKey.equals("shipNm")) {
									returnParam.put(mapKey,
											pCrypto.Decrypt("normal", String.valueOf(returnParam.get(mapKey)), "", 0));
								}
							}
						}

						continue;
					}

					if (entryObj instanceof List) {
						List<Object> list = (List<Object>) entryObj;
						for (Object obj : list) {
							if (obj instanceof Map) {
								if (obj != null) {
									Map<String, Object> item = (Map<String, Object>) obj;
									Set<?> itemSet = item.keySet();
									for (Iterator<?> itemIter = itemSet.iterator(); itemIter.hasNext();) {
										String itemKey = (String) itemIter.next();
										if (!itemKey.equals("userPw")
												&& String.valueOf(item.get(itemKey)).indexOf(replaceStr) >= 0) {
											item.put(itemKey, String.valueOf(item.get(itemKey)).replaceAll(replaceStr,
													Matcher.quoteReplacement("$")));
										}
										if (this.activeProfile.equals("prod")) {

											if (itemKey.equals("collectionDt")) {
												log.debug("collectionDt : {}", String.valueOf(item.get(itemKey)));
												item.put(itemKey, pCrypto.Decrypt("normal",
														String.valueOf(item.get(itemKey)), "", 0));
											}
											if (itemKey.equals("collectionPos")) {
												item.put(itemKey, pCrypto.Decrypt("normal",
														String.valueOf(item.get(itemKey)), "", 0));
											}
											if (itemKey.equals("dataCoordX")) {
												log.debug("dataCoordX");
												item.put(itemKey, pCrypto.Decrypt("normal",
														String.valueOf(item.get(itemKey)), "", 0));
												log.debug(itemKey + ":{}", item.get(itemKey));
											}
											if (itemKey.equals("dataCoordY")) {
												log.debug("dataCoordY");
												item.put(itemKey, pCrypto.Decrypt("normal",
														String.valueOf(item.get(itemKey)), "", 0));
												log.debug(itemKey + ":{}", item.get(itemKey));
											}

											if (itemKey.equals("shipNm")) {
												log.debug("shipNm");
												item.put(itemKey, pCrypto.Decrypt("normal",
														String.valueOf(item.get(itemKey)), "", 0));
												log.debug(itemKey + ":{}", item.get(itemKey));
											}
										}
									}
								}

								continue;
							}

							if (!(obj instanceof mil.gdl.nis.user.vo.UserVo)
									&& !(obj instanceof mil.gdl.nis.cmmn.vo.CodeVo)
									&& !(obj instanceof mil.gdl.nis.cmmn.vo.FileVo) && !(obj instanceof PageInfo)) {
								Class<? extends Object> returnClazz = (Class) obj.getClass();
								Field[] fields = returnClazz.getDeclaredFields();
								for (Field f : fields) {
									f.setAccessible(true);
									if (!f.getName().equals("userPw")) {
										String fileVal = String.valueOf(getValueFromField(f, returnClazz, obj));
										if (fileVal.indexOf(replaceStr) >= 0) {
											f.set(obj, fileVal.replaceAll(replaceStr, Matcher.quoteReplacement("$")));
										}
									}
									if (this.activeProfile.equals("prod")) {
										if (f.getName().equals("collectionDt")) {
											String fileVal = String.valueOf(getValueFromField(f, returnClazz, obj));
											f.set(obj, pCrypto.Decrypt("normal", fileVal, "", 0));
										}
										if (f.getName().equals("collectionPos")) {
											String fileVal = String.valueOf(getValueFromField(f, returnClazz, obj));
											f.set(obj, pCrypto.Decrypt("normal", fileVal, "", 0));
										}
										if (f.getName().equals("dataCoordX")) {
											String fileVal = String.valueOf(getValueFromField(f, returnClazz, obj));
											f.set(obj, pCrypto.Decrypt("normal", fileVal, "", 0));
										}
										if (f.getName().equals("dataCoordY")) {
											String fileVal = String.valueOf(getValueFromField(f, returnClazz, obj));
											f.set(obj, pCrypto.Decrypt("normal", fileVal, "", 0));
										}
										if (f.getName().equals("shipNm")) {
											String fileVal = String.valueOf(getValueFromField(f, returnClazz, obj));
											f.set(obj, pCrypto.Decrypt("normal", fileVal, "", 0));
										}
									}
								}
							}
						}

						continue;
					}

					if (!(entryObj instanceof mil.gdl.nis.user.vo.UserVo)
							&& !(entryObj instanceof mil.gdl.nis.cmmn.vo.CodeVo)
							&& !(entryObj instanceof mil.gdl.nis.cmmn.vo.FileVo) && !(entryObj instanceof PageInfo)) {
						Class<? extends Object> returnClazz = (Class) entryObj.getClass();
						Field[] fields = returnClazz.getDeclaredFields();
						for (Field f : fields) {
							f.setAccessible(true);
							if (!f.getName().equals("userPw")) {
								String fileVal = String.valueOf(getValueFromField(f, returnClazz, entryObj));
								if (fileVal.indexOf(replaceStr) >= 0) {
									f.set(entryObj, fileVal.replaceAll(replaceStr, Matcher.quoteReplacement("$")));
								}
							}
							if (this.activeProfile.equals("prod")) {
								if (f.getName().equals("collectionDt")) {
									String fileVal = String.valueOf(getValueFromField(f, returnClazz, entryObj));
									f.set(entryObj, pCrypto.Decrypt("normal", fileVal, "", 0));
								}
								if (f.getName().equals("collectionPos")) {
									String fileVal = String.valueOf(getValueFromField(f, returnClazz, entryObj));
									f.set(entryObj, pCrypto.Decrypt("normal", fileVal, "", 0));
								}
								if (f.getName().equals("dataCoordX")) {
									String fileVal = String.valueOf(getValueFromField(f, returnClazz, entryObj));
									f.set(entryObj, pCrypto.Decrypt("normal", fileVal, "", 0));
								}
								if (f.getName().equals("dataCoordY")) {
									String fileVal = String.valueOf(getValueFromField(f, returnClazz, entryObj));
									f.set(entryObj, pCrypto.Decrypt("normal", fileVal, "", 0));
								}
								if (f.getName().equals("shipNm")) {
									String fileVal = String.valueOf(getValueFromField(f, returnClazz, entryObj));
									f.set(entryObj, pCrypto.Decrypt("normal", fileVal, "", 0));
								}
							}
						}
					}
				}

				if (trnc != null) {
					int[] isExistOfMsg = { 0 };
					map.keySet().forEach(key -> {
						if (key.equals("success_msg") || key.equals("fail_msg") || key.toString().startsWith("valid")) {
							isExistOfMsg[0] = isExistOfMsg[0] + 1;
							return;
						}
					});
					log.debug("isExistOfMsg:{}", isExistOfMsg);
					if (isExistOfMsg[0] == 0) {
						throw new BizException(ErrorCode.NOT_EXIST_MSG);
					}
				}

				if (page != null) {
					if (map.containsKey("list")) {
						for (Object obj : joinPoint.getArgs()) {
							if (obj instanceof Map) {
								Map<String, Object> mapParam = (Map<String, Object>) obj;
								if (mapParam.containsKey("pageInfo")) {
									map.put("pageInfo", mapParam.get("pageInfo"));
								}
							}
						}
					}

					if (map.size() > 1) {
						map.forEach((key, value) -> {

							if (value instanceof List)
								;
						});
					}
				}
			} else if (returnValue instanceof ModelAndView) {
				ModelAndView mv = (ModelAndView) returnValue;
				Map<String, Object> m = mv.getModel();
				if (m.containsKey("imgMov") || m.containsKey("dataSet")) {
					DatasetVo vo = null;
					if (m.containsKey("imgMov")) {
						vo = (DatasetVo) m.get("imgMov");
					} else if (m.containsKey("dataSet")) {
						vo = (DatasetVo) m.get("dataSet");
					}
					if (vo != null) {
						Class<? extends Object> returnClazz = (Class) vo.getClass();
						Field[] fields = returnClazz.getDeclaredFields();
						for (Field f : fields) {
							f.setAccessible(true);
							if (this.activeProfile.equals("prod")) {
								if (f.getName().equals("collectionDt")) {
									String fileVal = String.valueOf(getValueFromField(f, returnClazz, vo));
									f.set(vo, pCrypto.Decrypt("normal", fileVal, "", 0));
								}
								if (f.getName().equals("collectionPos")) {
									String fileVal = String.valueOf(getValueFromField(f, returnClazz, vo));
									f.set(vo, pCrypto.Decrypt("normal", fileVal, "", 0));
								}
								if (f.getName().equals("dataCoordX")) {
									String fileVal = String.valueOf(getValueFromField(f, returnClazz, vo));
									if (!StringUtil.nullConvert(fileVal).equals("")) {
										f.set(vo, pCrypto.Decrypt("normal", fileVal, "", 0));
									} else {

										f.set(vo, StringUtil.nullConvert(fileVal));
									}
								}

								if (f.getName().equals("dataCoordY")) {
									String fileVal = String.valueOf(getValueFromField(f, returnClazz, vo));
									if (!StringUtil.nullConvert(fileVal).equals("")) {
										f.set(vo, pCrypto.Decrypt("normal", fileVal, "", 0));
									} else {

										f.set(vo, StringUtil.nullConvert(fileVal));
									}
								}

								if (f.getName().equals("shipNm")) {
									String fileVal = String.valueOf(getValueFromField(f, returnClazz, vo));
									f.set(vo, pCrypto.Decrypt("normal", fileVal, "", 0));
								}
							}
						}
					}
				}
			}

			Log logData = methodSignature.getMethod().<Log>getAnnotation(Log.class);
			if (logData != null) {
				String url = null;
				GetMapping getMappingData = methodSignature.getMethod().<GetMapping>getAnnotation(GetMapping.class);
				if (getMappingData != null) {
					url = getMappingData.value()[0];
				}
				PostMapping postMappingData = methodSignature.getMethod().<PostMapping>getAnnotation(PostMapping.class);
				if (postMappingData != null) {
					url = postMappingData.value()[0];
				}
				RequestMapping reqMappingData = methodSignature.getMethod()
						.<RequestMapping>getAnnotation(RequestMapping.class);
				if (reqMappingData != null) {
					url = reqMappingData.value()[0];
				}
				log.debug("url:{}", url);
				Map<String, Object> logMap = new HashMap<>();
				if (url != null && url.startsWith("/fileId")) {
					if ((joinPoint.getArgs()).length > 0) {
						logMap.put("fileId", joinPoint.getArgs()[0]);
						logMap.put("fileSeq", joinPoint.getArgs()[1]);
					}

					HttpServletRequest request = null;
					for (Object obj : joinPoint.getArgs()) {
						if (obj instanceof HttpServletRequest
								|| obj instanceof org.springframework.web.multipart.MultipartHttpServletRequest) {
							request = (HttpServletRequest) obj;
						}
					}
					String ip = request.getHeader("X-FORWARDED-FOR");

					if (ip == null) {
						ip = request.getRemoteAddr();
					}
					logMap.put("userId", this.sessionData.getUserVo().getUserId());
					logMap.put("useMenu", "/fileId");
					logMap.put("connectIp", ip);
					logMap.put("contents", "");
					this.logService.insertLog(logMap);

				}

			}

		} catch (Exception e) {
			log.error("ControllerAspect error:", e);
			e.printStackTrace();
		}
		log.debug("return:{}", returnValue);
	}

	@AfterThrowing(pointcut = "commonPointcut()", throwing = "ex")
	public void errorInterceptor(JoinPoint joinPoint, Throwable ex) throws IOException {
		ex.printStackTrace();
	}

	@Around("@annotation(mil.gdl.nis.annotation.NoAuth) && @ annotation(target)")
	public Object calllNoAuth(ProceedingJoinPoint joinPoint, NoAuth target) throws Throwable {
		log.debug("ControllerAspect calllNoAuth:" + joinPoint.getSignature().getDeclaringTypeName() + "."
				+ joinPoint.getSignature().getName());

		return joinPoint.proceed();
	}
}
