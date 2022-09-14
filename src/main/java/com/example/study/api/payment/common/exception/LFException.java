package com.example.study.api.payment.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LFException extends RuntimeException{
    private static final Logger LOGGER = LoggerFactory.getLogger(LFException.class);

    private ErrorMessages code = ErrorMessages.CODE_1;
    private List<String> args = new ArrayList<>();
    private Map<String, Object> map = new HashMap<>();

    public ErrorMessages getCode() {
        return code;
    }

    public List<String> getArgs() {
        return args;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    public String getMessage() {
        String message;
        if (args.size() > 0) {
            try{
                message = String.format(code.getErrorMessage(), args.toArray());
            } catch(Exception e) {
                message = code.getErrorMessage();
            }
        } else {
            message = code.getErrorMessage();
        }

        return message;
    }

    /**
     * 기본 메시지 출력하는 Exception "시스템 오류입니다."
     */
    public LFException() {
        super();
        this.code = ErrorMessages.CODE_1;
    }

    /**
     * 사용법
     *
     * <code>
     *
     * new LFException(CODE_1);
     * </code>
     *
     * @param error
     */
    public LFException(ErrorMessages error) {
        super();
        this.code = error;
    }

    /**
     * 사용법
     *
     * <code>
     *
     * new LFException(CODE_4, "placeholder");
     * </code>
     *
     * @param error
     * @param arg
     */
    public LFException(ErrorMessages error, String arg) {
        super();
        this.code = error;
        args.add(arg);
    }

    /**
     * 사용법
     *
     * <code>
     *
     * new LFException(CODE_4, 1);
     * </code>
     *
     * @param error
     * @param var
     */
    public LFException(ErrorMessages error, int var) {
        super();
        this.code = error;
        args.add(Integer.toString(var));
    }

    /**
     * 사용법
     *
     * <code>
     *
     * new LFException(CODE_4, 1, 2);
     * </code>
     *
     * @param error
     * @param var1
     * @param var2
     */
    public LFException(ErrorMessages error, int var1, int var2) {
        super();
        this.code = error;
        args.add(Integer.toString(var1));
        args.add(Integer.toString(var2));
    }

    /**
     * 사용법
     *
     * <code>
     *
     * new LFException(CODE_4, "placeholder1", "placeholder2");
     * </code>
     *
     * @param error
     * @param arg
     */
    public LFException(ErrorMessages error, String arg, String arg2) {
        super();
        this.code = error;
        args.add(arg);
        args.add(arg2);
    }

    /**
     * 사용법
     *
     * <code>
     *
     * new LFException(CODE_4, "placeholder1", "placeholder2", "placeholder3");
     * </code>
     *
     * @param error
     * @param arg
     */
    public LFException(ErrorMessages error, String arg, String arg2, String arg3) {
        super();
        this.code = error;
        args.add(arg);
        args.add(arg2);
        args.add(arg3);
    }

    /**
     *
     * 인자가 3개 이상 오는 경우 사용
     *
     * <code>
     *
     * new LFException(CODE_3001, Arrays.asList(new String[] {"reason1", "reason2", "reason3", "reason4"})));
     * </code>
     *
     * @param error
     * @param args
     */
    public LFException(ErrorMessages error, List<String> args) {
        super();
        this.code = error;
        this.args = args;
    }

    /**
     * 메시지 추가
     * throw new LFException("예외에 관한 설명");
     *
     * @param message
     */
    public LFException(String message) {
        super(message);
        this.code = ErrorMessages.CODE_1;
    }

    public void setExceptionData(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * 예외 전파 + 메시지 추가 throw new LFException("예외에 관한 설명", e);
     *
     * @param e
     */
    public LFException(Exception e, String message) {
        super(message, e);

        //exception is DB Exception일 경우 ErrorMessages.CODE_999로 바꿔 주기!!
        if (e instanceof org.springframework.dao.DataAccessException) {
            this.code = ErrorMessages.CODE_999;
            try {
                if ((Exception) e.getCause() instanceof SQLException) {
                    SQLException se = (SQLException) (Exception) e.getCause();
                    this.args.add(se.getErrorCode() + "");
                    this.args.add(se.getMessage());
                } else {
                    LOGGER.info("처리되지 않은 DB Exception : {}", e.getClass());
                }
            } catch (Exception ee) {
                ee.printStackTrace(System.err);
            }
        } else if(e instanceof org.springframework.transaction.CannotCreateTransactionException) {
            this.code = ErrorMessages.CODE_5;
        } else {
            this.code = ErrorMessages.CODE_1;
        }
    }

    /**
     * 예외 전파
     *
     * <code>
     * throw new LFException(e);
     * <code>
     *
     * @param e
     */
    public LFException(Exception e) {
        this(e, "");
    }

    /**
     * 정의된 코드로 예외 전파
     *
     * <code>
     * throw new LFException(e, CODE_1, "srl");
     * <code>
     *
     * @param e
     */
    public LFException(Exception e, ErrorMessages code, String arg) {
        super(e);
        this.code = code;
        if(arg != null) {
            args.add(arg);
        }
    }

    public LFException(Exception e, ErrorMessages code) {
        this(e, code, null);
    }

    /**
     * LFException data를 함께 전달
     *
     * @param e
     * @param map
     */
    public LFException(Exception e, Map<String, Object> map) {
        this(e, "");
        this.map = map;
    }

    /**
     * LFException data를 함께 전달
     *
     * @param te
     * @param map
     */
    public LFException(LFException te, Map<String, Object> map) {
        super(te);
        this.code = te.code;
        this.args = te.args;

        this.map = map;
    }
}
