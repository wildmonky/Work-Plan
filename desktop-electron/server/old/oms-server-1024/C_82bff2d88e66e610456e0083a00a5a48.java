import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_82bff2d88e66e610456e0083a00a5a48
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.unix2Date(mapRow.get("LOG_TIME").toString(),"yyyy-MM-dd HH:mm");
    }
}
