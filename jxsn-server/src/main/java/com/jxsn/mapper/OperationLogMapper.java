package com.jxsn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxsn.entity.OperationLog;
import com.jxsn.vo.GuideLogVO;
import com.jxsn.vo.OperationLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    @Select("""
            SELECT
                log_id AS logId,
                session_id AS sessionId,
                step_index AS stepIndex,
                CONCAT('第', step_index, '步操作') AS operationName,
                real_time_value AS paramValue,
                CASE WHEN is_correct = 1 THEN TRUE ELSE FALSE END AS isCorrect,
                ai_feedback AS aiFeedback,
                op_time AS opTime
            FROM operation_log
            WHERE session_id = #{sessionId}
            ORDER BY step_index ASC, op_time ASC
            """)
    List<OperationLogVO> selectLogListBySessionId(@Param("sessionId") Long sessionId);

    @Select("""
            <script>
            SELECT
                o.log_id AS logId,
                o.session_id AS sessionId,
                o.session_id AS recordId,
                u.real_name AS studentName,
                u.username AS studentNo,
                c.process_name AS processName,
                o.step_index AS stepIndex,
                o.real_time_value AS realTimeValue,
                o.is_correct AS isCorrect,
                CASE
                    WHEN o.is_correct = 1 THEN '正常'
                    ELSE '异常'
                END AS status,
                CASE
                    WHEN o.real_time_value LIKE 'temperature=%' THEN '温度异常'
                    WHEN o.real_time_value LIKE 'humidity=%' THEN '湿度异常'
                    WHEN o.real_time_value LIKE 'pressure=%' THEN '压力异常'
                    WHEN o.real_time_value LIKE 'duration=%' THEN '时长异常'
                    WHEN o.real_time_value LIKE 'microbe=%' THEN '微生物浓度异常'
                    WHEN o.real_time_value LIKE 'seal_status=%' THEN '密封状态异常'
                    ELSE '操作参数异常'
                END AS errorType,
                o.ai_feedback AS aiFeedback,
                o.op_time AS opTime
            FROM operation_log o
            LEFT JOIN training_session s ON o.session_id = s.session_id
            LEFT JOIN sys_user u ON s.student_id = u.user_id
            LEFT JOIN scene_template t ON s.template_id = t.template_id
            LEFT JOIN craft_process_definition c ON t.process_id = c.process_id
            <where>
                <if test="studentName != null and studentName != ''">
                    AND u.real_name LIKE CONCAT('%', #{studentName}, '%')
                </if>
                <if test="studentNo != null and studentNo != ''">
                    AND u.username LIKE CONCAT('%', #{studentNo}, '%')
                </if>
                <if test="processName != null and processName != ''">
                    AND c.process_name LIKE CONCAT('%', #{processName}, '%')
                </if>
                <if test="onlyError != null and onlyError == true">
                    AND o.is_correct = 0
                </if>
            </where>
            ORDER BY o.op_time DESC, o.log_id DESC
            </script>
            """)
    List<GuideLogVO> selectGuideLogList(
            @Param("studentName") String studentName,
            @Param("studentNo") String studentNo,
            @Param("processName") String processName,
            @Param("onlyError") Boolean onlyError
    );
}