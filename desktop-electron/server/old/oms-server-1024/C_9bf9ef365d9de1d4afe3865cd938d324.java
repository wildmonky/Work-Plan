import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_9bf9ef365d9de1d4afe3865cd938d324
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.getHttpVar("table_id");
    }
}
