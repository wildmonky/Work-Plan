import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_cda57ddf9243352645b7b19b8a930d6d
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.getMsgByKey("1=>替换旧图;2=>新增图档;",mapRow.get("F_TDZT"));

    }
}
