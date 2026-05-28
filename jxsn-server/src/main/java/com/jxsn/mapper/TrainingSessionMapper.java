package com.jxsn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxsn.entity.TrainingSession;
import com.jxsn.vo.TrainingDetailVO;
import com.jxsn.vo.TrainingRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TrainingSessionMapper extends BaseMapper<TrainingSession> {

    @Select("""
        <script>
        SELECT
            s.session_id AS recordId,
            s.session_id AS sessionId,
            u.real_name AS studentName,
            u.username AS studentNo,
            c.process_name AS processName,
            CASE
                WHEN s.session_status = '已完成' THEN 100
                WHEN s.session_status = '进行中' THEN 65
                ELSE 0
            END AS progress,
            s.session_status AS status,
            IFNULL(w.warningCount, 0) AS warningCount,
            IFNULL(l.latestOperation, '暂无操作记录') AS latestOperation,
            IFNULL(l.updateTime, s.start_time) AS updateTime
        FROM training_session s
        LEFT JOIN sys_user u ON s.student_id = u.user_id
        LEFT JOIN scene_template t ON s.template_id = t.template_id
        LEFT JOIN craft_process_definition c ON t.process_id = c.process_id
        LEFT JOIN (
            SELECT
                session_id,
                COUNT(*) AS warningCount
            FROM operation_log
            WHERE is_correct = 0
            GROUP BY session_id
        ) w ON s.session_id = w.session_id
        LEFT JOIN (
            SELECT
                session_id,
                real_time_value AS latestOperation,
                op_time AS updateTime
            FROM (
                SELECT
                    session_id,
                    real_time_value,
                    op_time,
                    ROW_NUMBER() OVER (
                        PARTITION BY session_id
                        ORDER BY op_time DESC, log_id DESC
                    ) AS rn
                FROM operation_log
            ) temp
            WHERE rn = 1
        ) l ON s.session_id = l.session_id
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
            <if test="startTime != null and startTime != ''">
                AND s.start_time &gt;= #{startTime}
            </if>
            <if test="endTime != null and endTime != ''">
                AND s.start_time &lt;= #{endTime}
            </if>
        </where>
        ORDER BY updateTime DESC
        </script>
        """)
    List<TrainingRecordVO> selectTrainingRecordList(
            @Param("studentName") String studentName,
            @Param("studentNo") String studentNo,
            @Param("processName") String processName,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );

    @Select("""
        SELECT
            s.session_id AS recordId,
            s.session_id AS sessionId,
            u.real_name AS studentName,
            u.username AS studentNo,
            c.process_name AS processName,
            CASE
                WHEN s.session_status = '已完成' THEN 100
                WHEN s.session_status = '进行中' THEN 65
                ELSE 0
            END AS progress,
            s.session_status AS status
        FROM training_session s
        LEFT JOIN sys_user u ON s.student_id = u.user_id
        LEFT JOIN scene_template t ON s.template_id = t.template_id
        LEFT JOIN craft_process_definition c ON t.process_id = c.process_id
        WHERE s.session_id = #{sessionId}
        """)
    TrainingDetailVO selectTrainingDetail(@Param("sessionId") Long sessionId);
}