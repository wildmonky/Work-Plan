import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_9d9eef6138666519fd99c80d0a40163c
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.getMapValueByKey("elink;电话;短信告警;口头;现场;巡视;值班电话;",mapRow.get("XXLZFS"));

    }
}
