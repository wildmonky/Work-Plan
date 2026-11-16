import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_52c7c06fd6808fc11734723ad862b5f5
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.getMapValueByKey("1=>替换旧图;2=>新增图档;",mapRow.get("F_TDZT"));

    }
}
